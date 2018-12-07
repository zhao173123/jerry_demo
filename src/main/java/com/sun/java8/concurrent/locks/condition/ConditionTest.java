package com.sun.java8.concurrent.locks.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 理解Condition
 * Condition是“显示锁”的条件队列，扩展了Object的wait、notify、notifyAll。
 * 提供给线程一种方式，使得该线程在调用await后执行挂起操作，直到线程等待的某个条件为真时被唤醒；
 * 条件队列必须和锁一起使用，因为对共享状态的访问多发生在多线程环境下。
 * Condition的实例必须和一个Lock绑定。因此，Condition一般作为Lock的内部类。
 *
 * Condition内部维护了等待队列的头节点和尾节点
 * 等待队列的作用是存放signal信号的线程，该线程被封装为Node节点后保存
 * 
 *AbstractQueuedSynchronizer.ConditionObject的实现：
 *
 	public class ConditionObject implements Condition,java.io.Serializable {
 		private static final long serialVersionUID = 1173984872572414699L;
 		//first node of condition queue
 		private transient Node firstWaiter;
 		//Last node of condition queue.
 		private transient Node lastWaiter;
 		
 	 	public ConditionObject() { }
 	 	
 	 	//添加到条件队列
 	 	private Node addConditionWaiter() {
            Node t = lastWaiter;
            //如果条件队列队尾节点waitStatus不为condition，说明该条件队列可能包含被取消的线程，
 			//unlinkCancelledWaiters()操作就是踢掉waitStatus不为condition的节点
            if (t != null && t.waitStatus != Node.CONDITION) {
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
            if (t == null)
                firstWaiter = node;
            else
                t.nextWaiter = node;
            lastWaiter = node;
            return node;
        }
        
        private void doSignal(Node first) {
            do{
                if((firstWaiter = first.nextWaiter) == null)//修改头节点，完成旧头节点的移除工作
                    lastWaiter = null;
                first.nextWaiter = null;
            }while(!transferForSignal(first) &&//将老的头结点，加入到AQS的等待队列中
                     (first = firstWaiter) != null);
        }
        
        //将条件队列的所有节点转移到AQS线程的等待队列
        private void doSignalAll(Node first) {
            lastWaiter = firstWaiter = null;
            do {
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                transferForSignal(first);
                first = next;
            } while (first != null);
        }

        private void unlinkCancelledWaiters() {
            Node t = firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                if (t.waitStatus != Node.CONDITION) {
                    t.nextWaiter = null;
                    if (trail == null)
                        firstWaiter = next;
                    else
                        trail.nextWaiter = next;
                    if (next == null)
                        lastWaiter = trail;
                }
                else
                    trail = t;
                t = next;
            }
        }

 		 //await操作释放锁，使其他线程可以获取锁；
 		 //步骤如下：
 		 //1.将当前线程加入condition锁队列 addConditionWaiter
 		 //2.释放锁 fullyRelease()，否则别的线程会无法拿到锁而发生死锁
 		 //3.自旋while挂起，直到被唤醒|超时|CANCELLED
         //4.获取锁，acquireQueued(),并将自己从Condition的FIFO队列中释放，表明不再需要锁（因为已经拿到了锁）
		 public final void await() throws InterruptedException {
			 if (Thread.interrupted())
			 	throw new InterruptedException();
			 Node node = addConditionWaiter();
			 int savedState = fullyRelease(node);
			 int interruptMode = 0;
 			 while (!isOnSyncQueue(node)) {
			 	LockSupport.park(this);
			 	if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
			 		break;
			 }
			 if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
			 	interruptMode = REINTERRUPT;
			 if (node.nextWaiter != null) // clean up if cancelled
			 	unlinkCancelledWaiters();
			 if (interruptMode != 0)
			 	reportInterruptAfterWait(interruptMode);
		 }
        
        public final void signal() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;//取出头节点
            if (first != null)
                doSignal(first);
        }
 	}
 //尝试将Node的waitStatus从CONDITION置为0，如果失败直接返回false
 //当前节点调用enq，加入到AbstractQueuedSynchronizer队列
 //当前节点通过CAS机制将waitStatus置为SIGNAL
 //结论：某个await()的节点被唤醒之后并不意味着它后面的代码会立即执行，
 //它会被加入到AbstractQueuedSynchronizer队列的尾部，只有前面等待的节点获取锁全部完毕才能轮到它
 final boolean transferForSignal(Node node) {
	 if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
	 	return false;
	 Node p = enq(node);
	 int ws = p.waitStatus;
	 //如果该结点的状态为cancel或者修改waitStatus失败，则直接唤醒。
	 if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
	 	LockSupport.unpark(node.thread);
	 return true;
  }	
 *
 *
 * 本实例的过程分析：
 * 1.线程1调用lock，线程被加入AQS的等待队列
 * 2.线程1调用Condition.await()，该线程从AQS移除，对应操作是锁的释放
 * 3.接着马上进入到Condition的等待队列中，意味着该线程需要signal信号
 * 4.线程2，因为线程1释放了锁，被唤醒并判断可以获取锁，于是线程2获取锁并被加入到AQS的等待队列
 * 5.线程2调用signal方法，此时Condition的等待队列中只有线程1一个节点，于是它被取出来，并加入到AQS的
 * 	  等待队列中（注意，此时线程1并没有被唤醒）
 * 6.signal执行完毕，线程2调用unlock方法，释放锁。这个时候因为AQS中只有线程1，于是AQS释放锁后按头到尾的顺序唤醒线程时，
 *    线程1被唤醒，于是线程1恢复执行
 * 7.直到释放整个过程执行完毕
 *
 * @author jerry
 *
 */
public class ConditionTest {

	public static void main(String[] args) throws InterruptedException {
		final ReentrantLock lock = new ReentrantLock();
		final Condition condition = lock.newCondition();

		Thread thread1;
		thread1 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("我要等一个信号！");
				condition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.out.println("拿到一个信号！");
				lock.unlock();
			}
		}, "waitThread1");
		thread1.start();

		Thread thread2 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("我拿到了锁！");
				Thread.sleep(3000);
				condition.signalAll();
				System.out.println("我发了一个信号！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}, "signalThread");
		thread2.start();
		
	}
}

