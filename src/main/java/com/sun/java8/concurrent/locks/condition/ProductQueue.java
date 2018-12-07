package com.sun.java8.concurrent.locks.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Condition实现生产则／消费者模型
 * 
 * Condition:一个多线程间协调通信的工具类,
 * 使得某个或者某些线程一起等待某个条件（Condition）,
 * 只有当该条件具备(signal或者signalAll方法被带调用)时，这些等待线程才会被唤醒，从而重新争夺锁。
 * 
 * @author jerry
 *
 * @param <T>
 */
public class ProductQueue<T> {
	
	public final T[] items;
	
	private final Lock lock = new ReentrantLock();
		
	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();
	
	private int head, tail, count;
	
	public ProductQueue(int maxSize){
		this.items = (T[])new Object[maxSize];
	}
	
	public ProductQueue(){
		this(10);
	}
	
	//生产put()需要队列不满，如果满了就挂起（await()），直到收到notFull的信号。
	public void put(T t) throws InterruptedException{
		lock.lock();
		try{
			while(count == getCapacity()){
				/**
				 * Condition.await():
				 * 释放锁，然后挂起线程；一旦满足条件就被唤醒，再次获取锁
				 public final void await() throws InterruptedException {
			            if (Thread.interrupted())//检查中断状态（线程是否有中断操作，有抛出InterruptedException）
			                throw new InterruptedException();
			            Node node = addConditionWaiter();//将当前线程加入Condition锁队列（FIFO）:Condition自己维护的一个链表
			            int savedState = fullyRelease(node);//释放当前线程占有的锁
			            int interruptMode = 0;
			            //释放完毕后，遍历AQS的队列，看当前节点是否在队列中,
			            //不在,说明它还没有竞争锁的资格，所以继续将自己沉睡,直到它被加入到队列中;
			            while (!isOnSyncQueue(node)) {//自旋挂起，直到被唤醒或超时或cancelled
			                LockSupport.park(this);
			                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
			                    break;
			            }
			            ////被唤醒后，重新开始正式竞争锁，同样，如果竞争不到还是会将自己沉睡，等待唤醒重新开始竞争。
			            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)//获取锁
			                interruptMode = REINTERRUPT;
			            if (node.nextWaiter != null) // clean up if cancelled
			                unlinkCancelledWaiters();
			            if (interruptMode != 0)
			                reportInterruptAfterWait(interruptMode);
			      }
				 * 1.将当前线程加入Condition锁队列，这里进入的是Condition的FIFO队列。进行2
				 * 2.释放锁。
				 * 3.自旋挂起（while），直到被唤醒或者超时或者cancelled等
				 * 4.获取锁（acquireQueued），并将自己从Condition的FIFO队列中释放，表明自己不再需要锁。
				 * 
				 private Node addConditionWaiter() {//
		            Node t = lastWaiter;
		            // If lastWaiter is cancelled, clean out.
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
				 * 
				 * 
				 * 
				 */
				notFull.await();
			}
			items[tail] = t;
			if(++tail == getCapacity()){
				tail = 0;
			}
			++count;
			notEmpty.signalAll();
		}finally{
			lock.unlock();
		}
	}
	
	//消费take()需要 队列不为空，如果为空就挂起（await()）,直到收到notEmpty的信号
	public T take() throws InterruptedException{
		lock.lock();
		try{
			while(count == 0){
				notEmpty.await();
			}
			T ret = items[head];
			items[head] = null;
			if(++head == getCapacity()){
				head = 0;
			}
			--count;
			notFull.signalAll();
			return ret;
		}finally{
			lock.unlock();
		}
	}
	
	public int getCapacity(){
		return items.length;
	}
	
	public int size(){
		lock.lock();
		try{
			return count;
		}finally{
			lock.unlock();
		}
	}
}
