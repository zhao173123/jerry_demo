package com.sun.java8.concurrent.locks.reentrantlock;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * ReentrantLock是一个可重入得独占锁，支持公平和非公平两种模式。
 *
 * public class ReentrantLock implements Lock, java.io.Serializable {

    private final Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {

        abstract void lock();//抽象方法,供子类实现；

        final boolean nonfairTryAcquire(int acquires) {//非公平的获取锁
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {//首次获取同步状态
                if (compareAndSetState(0, acquires)) {//只要设置成功就获取到锁
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {//再次获取同步状态(可重入的关键)
                //如果是获取锁的线程再次请求，则将同步状态值进行增加并返回true，表示获取同步状态成功。
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
         protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {//当同步状态为0时，将占有线程设置为null
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);//更新同步状态
            return free;
        }
         ......
    }

    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        final void lock() {
            if (compareAndSetState(0, 1))//首次获取锁成功
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);//申请加锁
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);//非公平获取锁
        }
    }
 *
 *
 * 非公平性的实现,实例:
 * Thread-1拥有锁，Thread-2和Thread-3在同步队列中，
 * 当Thread-1释放锁后会唤醒Thread-2，但是如果此时Thread-1重新申请锁，
 * 可能依然是Thread-1获取到锁。甚至这时候Thread-4也申请锁，Thread-4也可能比Thread-2先获取锁。
 static final class NonfairSync extends Sync {
    private static final long serialVersionUID = 7316153563782823691L;
    final void lock() {
        if (compareAndSetState(0, 1))//只要CAS更新同步状态成功就获取到锁。
            setExclusiveOwnerThread(Thread.currentThread());
        else //如果开始没有获取锁，还是公平锁一样进入等待队列
            acquire(1);
    }

    protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }
  }
 *
 * 公平性的实现
static final class FairSync extends Sync {
    private static final long serialVersionUID = -3000897897090466540L;
    //直接调用AQS的acquire
    //以独占模式获取对象，忽略中断。通过至少调用一次tryAcquire来实现此方法，并在成功时返回；
    //否则一直调用tryAcquire讲线程加入队列，线程可能重复被阻塞|不阻塞
    final void lock() {
        acquire(1);
    }
	protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        //这里没有一进来就直接进行CAS操作
        if (c == 0) {
            if (!hasQueuedPredecessors() &&//增加是否有前驱线程的判断，从而保证公平性
            	compareAndSetState(0, acquires)) {
	                setExclusiveOwnerThread(current);
	                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
    //公平锁相比非公平锁，这里的处理多了一步!hasQueuedPredecessors()；因为非公平锁开始的时候是不走tryAcquire的，而是直接cas操作判断
    //这里有2种情况返回true，
    //1.h==t的情况要么当前FIFO队列没有任务数据，要么只构建了head还没有向后链接任一个node，此时tail=head
    //2.(s=h.next)!=null&&s.thread==Thread.currentThread()：当前线程为正在等待的第一个Node中的线程
    public final boolean hasQueuedPredecessors() {
         Node t = tail;
         Node h = head;
         Node s;
         return h != t &&
         ((s = h.next) == null || s.thread != Thread.currentThread());
     }
}
 *
 *
 * 释放同步状态:
 * 当tryRelease操作成功后才能检查是否需要唤醒下一个继任节点，
 * 这里的前提是AQS队列的头结点需要锁(waitStatus!=0)，如果头结点需要锁，就开始检测下一个继任节点是否需要锁操作。
 * 释放锁lock.unlock();->sync.release(1);->
 * public final boolean release(int arg) {
        if (tryRelease(arg)) {//释放同步状态
            Node h = head;
            if (h != null && h.waitStatus != 0)//独占模式下这里表示SIGNAL
                unparkSuccessor(h);//唤醒后继节点
            return true;
        }
        return false;
    }
    //对于独占锁而言，ReentrantLock.Sync.tryRelease(int arg)展示了如何尝试释放锁的操作：
    //tryRelease(arg)操作，此操作里面总是尝试去释放锁，
    //如果成功，说明锁确实被当前线程持有，那么就看AQS队列中的头结点是否为空并且能否被唤醒，如果可以的话就唤醒继任节点
    protected final boolean tryRelease(int releases) {
	    int c = getState() - releases;
	    if (Thread.currentThread() != getExclusiveOwnerThread())
	        throw new IllegalMonitorStateException();
	    boolean free = false;
	    if (c == 0) {//是否完全释放了锁
	        free = true;
	        setExclusiveOwnerThread(null);
	    }
	    setState(c);
	    return free;
	}
 * 分析：
 * 1.判断持有锁的是否是当前线程，如果不是就throw new IllegalMonitorStateException
 * 	 （因为一个线程是不能释放另一个线程持有的锁），否则进行2
 * 2.将AQS状态位较少要释放的次数（对于独占锁就是1），如果剩余的状态位是0（也就是没有线程持有锁），那么当前线程就是最后一个持有锁的线程，
 *   清空AQS持有锁的独占线程。进行3
 * 3.剩余的状态位回写AQS，如果没有线程持有锁返回true，否则就是false
 *
 * private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;//获取当前节点等待状态
		if (ws < 0)
        	//清空头结点的waitStatus，也就是不再需要锁了
        	compareAndSetWaitStatus(node, Node.SIGNAL, 0);

        //从头结点的下一个节点开始寻找继任节点，当且仅当继任节点的waitStatus<=0才是有效继任节点，
        //否则将这些waitStatus>0（也就是CANCELLED的节点）从AQS队列中剔除
       //这里并没有从head->tail开始寻找，而是从tail->head寻找最后一个有效节点。
       //解释在这里 http://www.blogjava.net/xylz/archive/2010/07/08/325540.html#377512
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }

        //如果找到一个有效的继任节点，就唤醒此节点线程
        if (s != null)
            LockSupport.unpark(s.thread);
    }

 * @author jerry
 *
 */
public class ReentrantLockTest {

	private static Random r=new Random(47);
    private static int threadCount=10;
    private static ReentrantLock mut=new ReentrantLock();
    //公平性与否是针对获取锁而言的，如果一个锁是公平的，那么锁的获取顺序就应该符合请求的绝对时间顺序，也就是FIFO。
    //private static ReentrantLock mut = new ReentrantLock(true); //公平锁
    private static class Weight implements Runnable{//给苹果称重的任务
    	String name;
        public Weight(String name){
            this.name=name;
        }
        @Override
        public void run() {
            mut.lock();
            System.out.println(name+"放苹果！");
            System.out.println(name+"重量："+(r.nextInt(10)+3));
            System.out.println(name+"取苹果！");
            System.out.println("Thread name : " + Thread.currentThread().getName());
            if(r.nextInt()%2==0){run();}//递归调用
            mut.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads=new Thread[threadCount];
        for(int i=0;i<threadCount;i++){
            threads[i]=new Thread(new Weight("Weight-"+i),"Thread-"+i);
        }
        for(int i=0;i<threadCount;i++){
            threads[i].start();
            Thread.sleep(10);
        }
   }
}
