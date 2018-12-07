package com.sun.java8.concurrent.locks.aqs;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 实现一个排它锁 通过此实例来了解同步器的工作原理
 * 以此实现来参照分析AbstractQueuedSynchronizer
 *
 * 概要：
 * AQS中有3个核心字段，他们构成了CLH的FIFO队列基础。
 * private volatile int state;//描述有多少个线程取得了锁
 * private transient volatile Node head;//获取等待队列的头节点
 * private transient volatile Node tail;//获取等待队列的尾节点
 *
 * Node:
 *  SIGNAL:-1,等待状态值，指示继任的线程需要取消阻塞
 *  CONDITION:-2，等待状态值，指示线程由于阻塞而处于等待状态
 *  PROPAGATE:-3，等待状态值，指示处于共享状态下，下一次的acquire需要无条件的传播
 *  CANCELLED:1，等待状态值，节点因超时或中断已经取消
 *
 *  AQS是基于模板模式的实现，但是整个类中没有一个abstract的抽象方法，
 *  取而代之的是将需要子类实现的方法通过抛出UnsupportedOperationException异常。
 *  有5个方法需要子类实现：
 *  1.tryAcquire:尝试获取独占锁，通常在线程执行acquire时调用。
 *      锁竞争时不一定成功，成功返回true；
 *      失败则会将线程加入等待队列addWaiter，直到被其他线程释放的信号唤醒；
 *  2.tryRelease:尝试释放独占锁，通常在线程执行释放节点时调用。
 *      锁竞争时不一定成功，成功返回true；
 *  3.tryAcquireShared:尝试获取共享锁，通常在线程执行acquire时调用；
 *      锁竞争时不一定成功，成功返回true；
 *      如果失败，会将线程加入等待队，直到被其他线程释放的信号唤醒
 *  4.tryReleaseShared:尝试释放共享锁，通常在线程执行释放节点时调用；
 *      锁竞争时不一定成功，成功返回true；
 *  5.isHeldExclusively:当前同步器是否在独占模式下被线程占用，一般该方法表示是否被当前线程独占
 *
 *  只有前驱节点为head的节点才会尝试tryAcquire，其余都不会；
 *  并且成功后将原head置空，该节点置为头节点。
 *
 * *********************************************************************************************************************
 * 分析：核心方法
 * 1.public final void acquire(int arg){
 * 		if(!tryAcquire(arg) &&
 * 		    acquireQueued(addWaiter(Node.EXCLUSIVE), arg)){
 * 			selfInterrupt();
 * 		}
 * }  
 * 上述逻辑包括3部分，
 * 1.1 尝试获取锁tryAcquire，成功直接返回true；
 * 1.2 如果失败!tryAcquire，则将线程封装成一个node加入等待队列。等待队列是由Node对象组成的一个双向链表。
 * 	   每个Node都持有一个线程，Node源码如下：
            static final class Node{
                 static final Node SHARED = new Node();//表示Node节点是一个要获取共享锁的节点
                 static final Node EXCLUSIVE = null;//表示Node节点是一个要获取独占锁的节点
                 //以下4个常量代表Node节点的状态
                 static final int CANCELLED = 1;//代表该线程已经出现异常后正等待获取锁超时，取消等待，这样的节点会被直接移出等待队列
                 static final int SIGNAL = -1;//代表该节点的后续节点包含的线程需要被执行，执行UNPARK操作，取消阻塞，进行锁竞争
                 static final int CONDITION = -2;//代表该节点处于Condition.await()状态，在condition队列中
                 static final int PROPAGATE = -3;//表示当前场景下后续的acquireShared能够得以执行在condition队列中
                 //节点的等待状态，值为上述的4种；初始值为0，如果是头结点也为0；值为0表示节点在sync队列中等待获取锁
                 volatile int waitStatus;
                 volatile Node prev;
                 volatile Node next;
                 volatile Thread thread;
                 Node nextWaiter;//共享锁的下一个节点
                 //此节点进入等待获取锁的阻塞队列
                 Node(Thread thread, Node mode){
                     this.nextWaiter = mode;
                     this.thread = thread;
                 }
                 //此节点会进入Condition.await()队列
                 Node(Thread thread, int awaitStatus){
                     this.waitStatus = waitStatus;
                     this.thread = thread;
                 }
             }
 *  通过addWaiter，将该线程构造一个node，并加入等待者队列
 *  典型的实现是ReentrantLock，内部实现了Sync，在Sync基础上实现了公平锁FairSync和非公平锁NonFairSync。
 *      addWaiter源码如下：
 *      //将当前线程封装成Node对象。如果队列已经初始化，尝试将节点放入等待队列队尾；
 *      //如果队列为空或cas入列失败则执行enq
 * 	    private Node addWaiter(Node mode){
 * 		 	Node node = new Node(Thread.currentThread(), mode);
 * 			//尝试在尾部快速添加
 * 			Node pred = tail;
 * 			if(pred != null){//尾节点存在（队列已经初始化）
 * 				node.prev = pred;//当前节点的前驱节点指向尾节点
 * 				if(compareAndSetTail(pred, node)){//设置当前节点为尾节点
 * 					//如果CAS尝试成功，就说明"设置当前节点node的前驱"与"CAS设置tail"之间没有别的线程设置tail成功 
 * 					pred.next = node;//原尾节点的后继节点指向当前节点
 * 					return node;
 * 				}
 * 			}
 * 			enq(node);//否则，通过死循环来保证节点的正确添加  
 * 			return node;
 * 		  }
 *        //
 * 		  private Node enq(final Node node){
 * 				for(;;){//自旋
 * 					Node t = tail;
 * 					if(t == null){//尾节点不存在，同步队列为空，说明还没有初始化
 * 					    //如果设置失败，说明有其他需要入队等待的线程已经初始化了队列，则继续循环
 * 						if(compareAndSetHead(new Node())){//new Node()为头节点，尾节点=head
 * 							tail = head;
 * 						}
 * 					}else{//尾节点存在，队列不为空
 * 						node.prev = t;//尾节点指向当前节点的前驱节点
 * 						if(compareAndSetTail(t, node)){//设置当前节点为尾节点
 * 							t.next = node;//尾节点的后继节点指向当前节点
 * 							return t;//结束循环  
 * 						}
 * 					}
 * 				}
 * 		  }
 * 	上述addWaiter逻辑： 
 *   1.使用当前线程构造Node（概述）
 *   	对于一个节点需要做的就是将当前节点前驱节点指向尾节点（current.prev = tail），尾节点指向它(tail = current);
 *      原有尾节点的后继节点指向它（t.next = current）。
 *      而这些操作要求是原子的，上面操作是利用尾节点的设置来保证的，也就是compareAndSetTail来完成的；
 *      compareAndSetTail(Nodeexpect,Nodeupdate):需要传递当前线程“认为”的尾节点和当前节点，只有设置成功后，当前节点才正式与之前的尾节点建立关联
 *   2.先行尝试在队尾添加（步骤）
 *   	如果尾节点已经有了（pred != null），做如下操作：
 *   	（1）分配引用T指向尾节点
 *      （2）将节点的前驱节点更新为尾节点（current.prev=tail）
 *      （3）如果尾节点是T，那么将当尾节点设置为该节点（tail = current，原子更新）；
 *      这样可以以最短路径O(1)的效果来完成线程入队，是最大化减少开销的一种方式。
 *   3.如果队尾添加失败或者是第一个入队的节点 enq(final Node node)
 * 		如果是第1个节点，也就是sync队列没有初始化，那么会进入到enq这个方法，进入的线程可能有多个，
 * 		或者说在addWaiter中没有成功入队的线程都将进入enq这个方法。
 * 		enq的逻辑是确保进入的Node都会有机会顺序的添加到sync队列中，而加入的步骤如下：
 * 		（1）如果尾节点为空，那么原子化的分配一个头节点，并将尾节点指向头节点，这一步是初始化
 * 		（2）重复在addWaiter中做的工作，但是在一个while(true)的循环中，直到当前节点入队为止；
 * 			这里通过“死循环”来保证节点的正确添加，将并发添加节点的请求通过CAS“串形化”了；
 * 			PS：如果通过加锁同步的方式添加节点，线程必须获取锁后才能添加节点，必然导致其他线程等待加锁而阻塞，获取锁的线程
 * 				释放锁后阻塞的线程又会被唤醒，而线程的阻塞和唤醒必须依赖于系统内核来完成，因此程序的执行需要从用户态切换到核心态，
 * 				而这样的切换是非常耗时的操作。
 * 				通过“循环CAS”来添加节点，所有线程都不会被阻塞，而是不断失败重复，线程不需要进行锁同步，不仅消除了线程阻塞唤醒的开销
 * 				而且消除了加锁解锁的时间。但是CAS也有缺点，循环CAS通过不断尝试来添加节点，如果操作一直失败会占用处理器资源。
 *  
 *  1.3
 *  acquireQueued：将当前线程构造成节点node并加入sync队列，进入队列的每个线程都是一个节点node，从而形成了一个双向队列，类似CLH队列。
 *  进入sync队列后，就要进行锁的获取，或者说访问控制了。只有一个线程能够在同一时刻继续的运行，而其他的线程处于等待。
 *  节点和节点之间在循环检查的过程中不进行通信，而是简单的判断自己的前驱节点是否为头节点，这使得节点的释放规则符合FIFO
 *  并且便于对过早通知的处理（过早通知：前驱节点不是头节点的线程由于中断而被唤醒）
 *  当同步状态获取锁成功后，当前线程从acquire(int arg)返回，对于锁这种并发组件而言，代表着当前线程获取到了锁
 *  
 *  final boolean acquireQueued(final Node node, int arg){
 *  	boolean failed = true;
 *  	try{
 *  		boolean interrupted = false;
 *  		//节点进入同步队列后，进入“自旋”的过程；每个节点（或者说线程）都在自省观察
 *  		//当条件满足，获取到了同步状态，就可以从这个自旋过程中退出，否则就留在这个自旋中
 *  		for(;;){
 *  			final Node p = node.predecessor();//获取当前节点的前驱节点（prev）
 *  			//当前节点的前续节点是头结点说明它的前面没有等待节点，于是尝试获取锁，如果成功，则讲当前节点设置为头结点
 *  			if(p == head && tryAcquire(arg)){
 *  				//设置首节点是由获取同步状态成功的线程来完成的，由于只有一个线程能够成功的获取到同步状态
 *  				//因此设置头节点的方法并不需要使用CAS来保证，它只需要将首节点设置成为原首节点后继节点，并断开首节点的next引用即可
 *  				setHead(node);//设置当前节点为头节点
 *  				p.next = null;//help GC，断开引用
 *  				failed = false;
 *  				return interrupted;//从自旋中退出  
 *  			}
 *  			//如果当前节点的前驱节点不是头节点或是头结点但获取锁失败，那么将当前线程从线程调度器上摘下，也就是进入等待状态。
 *  			if(shouldParkAfterFailedAcquire(p, node) && //获取同步状态失败后判断是否需要阻塞或中断
 *  					parkAndCheckInterrupt()){//阻塞当前线程
 *  				interrupted = true;
 *  			}
 *  		}
 *  	}finally{
 *  		if(failed){
 *  			cancelAcquire(node);	
 *  		}
 *  	}
 *  } 
 *  //获取锁失败后，检查并更新节点状态
 *  private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {  
 *  	int ws = pred.waitStatus;//获取前驱节点的等待状态
 *  	//SIGNAL状态：前驱节点释放同步状态或者被取消，将会通知后继节点。因此，可以放心的阻塞当前线程，返回true。
 *  	if (ws == Node.SIGNAL)
 *  		return true;//可以进行park操作
 *  	if (ws > 0) {//前驱节点被取消了，相当于CANCELLED（只有它>0）。如果前驱节点都取消了，当前节点就不能被park，需要返回false循环执行。
 *  		 do {
            	node.prev = pred = pred.prev;//跳过当前节点的前驱节点(node.prev=pred指向node.prev.prev)
        	 } while (pred.waitStatus > 0);  
        	 pred.next = node;
        } else {//为PROPAGATE-3或SIGNAL0表示无状态，condition -2 表示在condition队列中，前驱节点调用await释放了锁，那么当前节点就不可以被park
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);//设置前驱节点等待状态为SIGNAL
        }
        return false; //不能进行park操作
    }
    //使用LockSupport.park来挂起线程，然后停在这里等待唤醒
    private final boolean parkAndCheckInterrupt() {
    	LockSupport.park(this);//阻塞当前线程
    	return Thread.interrupted(); 
    }
 *  acquireQueued逻辑包括：
 *  (1). 获取当前节点的前驱节点；
 *        需要获取当前节点的前驱节点，而头结点所对应的含义是当前占有锁且正在运行。
 *  (2). 当前驱节点是头结点并且能够获取状态，代表该当前节点占有锁；
 *        如果满足上述条件，那么代表能够占有锁，根据节点对锁占有的含义，设置头结点为当前节点。
 *  (3). 否则进入等待状态。
 *        如果没有轮到当前节点运行，那么将当前线程从线程调度器上摘下，也就是进入等待状态。
 *  
 *  节点的自旋示意图可以参考evernote
 *  
 *  2.释放同步状态
 *  当前线程获取同步状态并执行了相应逻辑之后，就需要释放同步状态，使得后续节点能够继续获取同步状态；
 *  通过调用同步器的release(int arg)方法可以释放同步状态，该方法在释放了同步状态之后，会"唤醒"其后继节点（进而使后继节点重新尝试获取同步状态）
 *  public final boolean release(int arg) {  
	    if (tryRelease(arg)) { //释放同步状态
	        Node h = head;  
	        if (h != null && h.waitStatus != 0) //头结点不为空且头节点不需要锁
	            unparkSuccessor(h); //唤醒后继节点
	        return true;  
	    }  
	    return false;  
	}
    //此时node是需要释放锁的头结点
	private void unparkSuccessor(Node node) {
	    int ws = node.waitStatus;//获取当前节点等待状态  
	    if (ws < 0)  
	        compareAndSetWaitStatus(node, ws, 0);//更新等待状态
        Node s = node.next;
        //如果s==null或者s被取消了，那么从队尾开始向前寻找一个waitStatus<=0的节点作为后续要唤醒的节点
	    if (s == null || s.waitStatus > 0) {
	        s = null;  
	        for (Node t = tail; t != null && t != node; t = t.prev)  
	            if (t.waitStatus <= 0)  
	                s = t;  
	    }  
	    if (s != null)  //如果找到一个有效的继任节点，就唤醒此节点线程
	        LockSupport.unpark(s.thread);//唤醒后继线程  
	}
 ***********************************************************************************************************************
 * 以上都是AQS的理论和源码分析，以下接着使用一个实际使用来解析代码的具体执行过程，让大家更容易理解AQS执行原理
 ***********************************************************************************************************************
 * 1.假设有3个线程请求锁，设为LockA|LockB|LockC
 * 2.LockA执行lock.lock()->acquire()成功，LockB|LockC阻塞
 *      public final void acquire(int arg) {
 *         if (!tryAcquire(arg) &&
 *             acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
 *             selfInterrupt();
 *     }
 *     private Node addWaiter(Node mode) {
 *         Node node = new Node(Thread.currentThread(), mode);
 *         // Try the fast path of enq; backup to full enq on failure
 *         Node pred = tail;
 *         if (pred != null) {
 *             node.prev = pred;
 *             if (compareAndSetTail(pred, node)) {
 *                 pred.next = node;
 *                 return node;
 *             }
 *         }
 *         enq(node);
 *         return node;
 *     }
 *    private Node enq(final Node node) {
 *         for (;;) {
 *             Node t = tail;
 *             if (t == null) { // Must initialize
 *                 if (compareAndSetHead(new Node()))
 *                     tail = head;
 *             } else {
 *                 node.prev = t;
 *                 if (compareAndSetTail(t, node)) {
 *                     t.next = node;
 *                     return t;
 *                 }
 *             }
 *         }
 *     }
 * 2.1 LockB获取锁失败，由于此时整个数据结构中没有任何数据，因此addWaiter的L253行pred为空，直接执行enq；
 *     而enqL265的t为null，因此new一个节点node为头结点，tail同样也是这个node。
 *     此时的数据结构为head=tail，假设地址为0x00
 *     {prev=null,next=null,thread=null,waitStatus=0,地址=0x00}
 *     接着继续执行enq，此时t不为空，执行else分支，将node追加到尾部，此时数据结构为:
 *     head {prev=null,next=0x01,thread=null,waitStatus=0,地址=0x00}
 *     tail {prev=0x00,next=null,thread=LockB,waitStatus=0,地址0x01}
 *     这样，LockB对应的node被加入数据结构，而head是一个什么也没有的空节点。
 * 2.2 接着LockC执行tryAcquire失败，执行addWaiter，此时pred不为null，所以直接将LockC加入到队尾
 *     此时数据结构为：
 *     head {prev=null,next=null,thread=null,waitStatus=0,地址=0x00}
 *     LockB{prev=0x00,next=0x02,thread=LockB,waitStatus=0,地址0x01}
 *     tail {prev=0x01,next=null,thread=LockC,waitStatus=0,地址0x02}
 *     至此，两个阻塞线程LockB和LockC对应的3个node就构建好了。
 * 3. 下面分析线程LockB|LockC的阻塞过程
 *    final boolean acquireQueued(final Node node, int arg) {
 *         boolean failed = true;
 *         try {
 *             boolean interrupted = false;
 *             for (;;) {
 *                 final Node p = node.predecessor();
 *                 if (p == head && tryAcquire(arg)) {
 *                     setHead(node);
 *                     p.next = null; // help GC
 *                     failed = false;
 *                     return interrupted;
 *                 }
 *                 if (shouldParkAfterFailedAcquire(p, node) &&
 *                     parkAndCheckInterrupt())
 *                     interrupted = true;
 *             }
 *         } finally {
 *             if (failed)
 *                 cancelAcquire(node);
 *         }
 *     }
 *     private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
 *         int ws = pred.waitStatus;
 *         if (ws == Node.SIGNAL)
 *            return true;
 *         if(ws > 0){
 *            do{
 *                node.prev=pred=pred.prev;
 *            }while(pred.waitStatus>0);
 *            pred.next=node;
 *         }else{
 *            compareAndSetWaitStatus(pred,ws,Node.SIGNAL);
 *         }
 *         return false;
 *     }
 * 3.1 LockB在tryAcquire失败后，它的前驱节点是head，在tryAcquire成功的情况下，
 *      执行acquireQueued的L299~304代码，将LockB节点设为head，原head的引用为null（GC）,failed=false没有失败。
 *      因此如果LockB执行tryAcquire成功数据结构变为：
 *      head {prev=0x00,next=0x02,thread=LockB,waitStatus=0,地址0x01}
 *      tail {prev=0x01,next=null,thread=LockC,waitStatus=0,地址0x02}
 *
 *      如果LockB执行tryAcquire失败的话，就要执行shouldParkAfterFailedAcquire了。
 *      shouldParkAfterFailedAcquire拿LockB的前驱节点(head)的waitStatus做了一个判断，
 *      因为此时waitStatus=0，所以执行shouldParkAfterFailedAcquire的L322，将head的waitStatus置为SIGNAL，即-1.然后返回false。
 *      此时数据结构变为：
 *      head {prev=null,next=0x01,thread=null,waitStatus=-1,地址=0x00}
 *      LockB{prev=0x00,next=0x02,thread=LockB,waitStatus=0,地址0x01}
 *      tail {prev=0x01,next=null,thread=LockC,waitStatus=0,地址0x02}
 *      继续执行for(;;)，如果acquire还是失败，依然执行shouldParkAfterFailedAcquire，此时ws=SIGNAL，返回true。
 *      继续执行：
 *      private final boolean parkAndCheckInterrupt() {
 *         LockSupport.park(this); //当前线程进入waiting状态，在此状态只有2种方式可以唤醒 unpark|interrupt
 *         return Thread.interrupted(); //Thread.interrupted()会清除当前线程的中断标记位
 *      }
 *      LockB调用LockSupport.park(this)挂起。
 *      至此，LockB阻塞，LockC和LockB的阻塞过程相同。
 * 4. 下面来看看释放锁的过程
 *    public final boolean release(int arg) {
 *      if (tryRelease(arg)) {
 *         Node h = head;
 *         if (h != null && h.waitStatus != 0)
 *             unparkSuccessor(h);
 *         return true;
 *      }
 *      return false;
 *    }
 *    当LockA释放时，如果释放成功（tryAcquire为true），那么就需要寻找头节点的下一个需要锁的后续节点并唤醒它。
 *    头节点不为null（队列已经被初始化），且头结点的waitStatus!=0（等待队列中有被park的节点）,则执行unparkSuccessor。
 *    private void unparkSuccessor(Node node) {
 *      int ws = node.waitStatus;
 *      if (ws < 0)
 *          compareAndSetWaitStatus(node, ws, 0);
 *      Node s = node.next;
 *      if (s == null || s.waitStatus > 0) {
 *          s = null;
 *          for (Node t = tail; t != null && t != node; t = t.prev)
 *              if (t.waitStatus <= 0)
 *                 s = t;
 *      }
 *      if (s != null)
 *         LockSupport.unpark(s.thread);
 *    }
 *    此时头结点的waitStatus=-1，CAS操作置为0；如果当前节点没有后续节点或者后续节点被CANCELLED，则从队尾开始向前找，
 *    找到最靠近对头的一个非CANCELLED节点LockB，并唤醒LockB。
 *    此时LockB被唤醒，LockB就会去尝试获取锁：
 *    final boolean acquireQueued(final Node node, int arg)
 *      for(;;){}
 *
 * *********************************************************************************************************************
 * 以上都是AQS支持独占锁的模式，下面看下AQS对共享模式的支持，和独占锁作对比可以对AQS有更好的理解。
 *     public final void acquireShared(int arg) {
 *         if (tryAcquireShared(arg) < 0)
 *             doAcquireShared(arg);
 *     }
 *     可以看到tryAcquireShared和独占锁的tryAcquire返回值就有不同，由子类实现，后面会根据子类的实现来分析
 *      tryAcquireShared<0表示获取资源失败
 *      tryAcquireShared=0表示获取成功，但是没有剩余资源
 *      tryAcquireShared>0表示获取成功，还有剩余资源
 *     先看下doAcquireShared的实现：
 *     private void doAcquireShared(int arg) {
 *         final Node node = addWaiter(Node.SHARED);
 *         boolean failed = true; //是否成功标志
 *         try {
 *             boolean interrupted = false; //等待过程中是否被中断过的标志
 *             for (;;) {
 *                 final Node p = node.predecessor();
 *                 if (p == head) {
 *                     int r = tryAcquireShared(arg);
 *                     if (r >= 0) {
 *                         setHeadAndPropagate(node, r);
 *                         p.next = null; // help GC
 *                         if (interrupted) //如果等待过程中被打断过，此时将中断补上。
 *                             selfInterrupt();
 *                         failed = false;
 *                         return;
 *                     }
 *                 }
 *                 //判断状态，寻找安全点，进入waiting状态，等着被unpark()或interrupt()
 *                 if (shouldParkAfterFailedAcquire(p, node) &&
 *                     parkAndCheckInterrupt())
 *                     interrupted = true;
 *             }
 *         } finally {
 *             if (failed)
 *                 cancelAcquire(node);
 *         }
 *     }
 *     执行过程：
 *     1.执行addWaiter，把tryAcquireShared<0的线程实例化出一个Node，构建一个FIFO队列，这里和独占锁一样
 *     2.拿当前节点的前驱节点p，只有前驱节点是head的节点才能tryAcquireShared，和独占锁一样
 *        但是如果tryAcquireShared执行成功后，共享模式会执行setHeadAndPropagate
 *        private void setHeadAndPropagate(Node node, int propagate) {
 *          Node h = head;
 *          setHead(node);
 *          //尝试唤醒后续节点
 *          //propagate > 0 : 说明还有许可可以被线程acquire;h.waitStatus < 0:PROPAGATE
 *          if (propagate > 0 || h == null || h.waitStatus < 0 ||
 *             (h = head) == null || h.waitStatus < 0) {
 *             Node s = node.next;
 *             //后继结点是共享模式或者为null
 *             if (s == null || s.isShared())
 *                 doReleaseShared();
 *          }
 *        }
 *        和独占锁最大的不同就在L427~434	,节点被设置为head后，如果它的后续节点是SHARED状态，那么将继续通过doReleaseShared尝试
 *        往后唤醒节点，实现共享状态的向后传播。
 *     3.前驱节点不是head的节点执行shouldParkAfterFailedAcquire和parkAndCheckInterrupt|for(;;)，
 *       shouldParkAfterFailedAcquire执行2次，当前线程阻塞，和独占锁一样
 * *********************************************************************************************************************
 * AQS的分析就到这里了，blog还需要配evernote得插图（流程图）更容易理解。
 *
 * @author jerry
 *
 */
public class Mutex implements Lock, Serializable {

	private static final long serialVersionUID = 5881078515920361592L;

	// 内部类，自定义同步器
	private static class Sync extends AbstractQueuedSynchronizer {

		private static final long serialVersionUID = -2414278228485625478L;

		// 是否处于占用状态
		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}

		// 当状态为0的时候获取锁
		@Override
		public boolean tryAcquire(int acquires) {
			assert acquires == 1;
			if (compareAndSetState(0, 1)) {
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}

		// 释放锁，将状态设置为0
		@Override
		protected boolean tryRelease(int release) {
			if(getState() == 0){
				throw new IllegalMonitorStateException();
			}
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}

		// 返回一个Condition，每一个Condition都包含一个Condition队列
		Condition newCondition() {
			return new ConditionObject();
		}

	}
	
	//仅需要将操作代理到Sync上即可
	private final Sync sync = new Sync();
	
	@Override
	public void lock() {
		sync.acquire(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);
	}

	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireNanos(1, unit.toNanos(time));
	}

	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition newCondition() {
		return sync.newCondition();
	}

	public boolean isLocked(){
		return sync.isHeldExclusively();
	}
	
	public boolean hasQueuedThreads(){
		return sync.hasQueuedThreads();
	}
	
	//获取等待的线程
	public Collection<Thread> getQueueThreads(){
		return sync.getQueuedThreads();
	}
}
