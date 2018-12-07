package com.sun.java8.concurrent.locks;

import java.util.concurrent.CountDownLatch;

/**
 * 
 * CountDownLatch:
 * 闭锁，可以延迟线程的进度，直到达到某个终点状态。
 * 简单来说，就相当于一道门关闭，所有的线程阻塞，当门打开的时候，
 * 所有线程执行；此时，门打开后就不能关闭了，所以叫闭锁。
 * 所有的线程都会在await处等待，直到countDown操作计数器-1，wait后的所有调用都将立即返回。
 * CountDownLatch锁是一次性的，确保在锁打开之前所有线程都处于阻塞状态，是一个正数计数器。
 * 	await()：等待计数器为0，所有await的线程都会阻塞到计数器为0执行或者等待线程中断或者超时
 * 	countDown()：对计数器作减法操作
 *
 * API： 
 * 1.public void await() throws InterruptedException 
 * 2.public boolean await(long timeout, TimeUnit unit) throws InterruptedException 
 * 3.public void countDown() 
 * 4.public long getCount() : 当前计数器，通常用于调试
 *
 *
 * 源码解析：
 * 1.将当前线程节点以共享方式加入AQS的CLH队列，进行2
 * 2.检查当前节点的前驱节点，如果是头结点并且闭锁计数为0就将当前节点置为头结点并唤醒后续节点，返回（结束线程阻塞）。否则进行3
 * 3.检查线程是否该被阻塞，如果是就阻塞park，直到被唤醒unpark；重复2
 * 4.检查2，3有异常或者中断就抛出异常
 * 	   public void await() throws InterruptedException {
 *    		sync.acquireSharedInterruptibly(1); //这里其实调用的是AQS的实现
 * 	   }
 *
 *     public final void acquireSharedInterruptibly(int arg)
 *             throws InterruptedException {
 *         if (Thread.interrupted()) //可以响应中断
 *             throw new InterruptedException();
 *         if (tryAcquireShared(arg) < 0)
 *             doAcquireSharedInterruptibly(arg);
 *     }
 *
 *     public final void acquireSharedInterruptibly(int arg)
 *             throws InterruptedException {
 *         if (Thread.interrupted())
 *             throw new InterruptedException();
 *         if (tryAcquireShared(arg) < 0)
 *             doAcquireSharedInterruptibly(arg);
 *     }
 *
 *     private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {
 *         final Node node = addWaiter(Node.SHARED);
 *         boolean failed = true;
 *         try {
 *             for (;;) {
 *                 final Node p = node.predecessor();
 *                 if (p == head) {
 *                     int r = tryAcquireShared(arg);
 *                     if (r >= 0) {
 *                         setHeadAndPropagate(node, r);
 *                         p.next = null; // help GC
 *                         failed = false;
 *                         return;
 *                     }
 *                 }
 *                 if (shouldParkAfterFailedAcquire(p, node) &&
 *                     parkAndCheckInterrupt())
 *                     throw new InterruptedException();
 *             }
 *         } finally {
 *             if (failed)
 *                 cancelAcquire(node);
 *         }
 *     }
 *     //对于闭锁而言第一次await的时候总是返回-1，因为此时state就是初始化的count值。
 *     protected int tryAcquireShared(int acquires) {
 *         return (getState() == 0) ? 1 : -1;
 *     }
 *
 *     //设置node为头结点，并唤醒后续节点（如果有的话）
 *     private void setHeadAndPropagate(Node node, int propagate) {
 *     		Node h = head;
 *     	    setHead(node);
 *     	    //对于闭锁CountDownLatch而言，propagate==1
 *     		if (propagate > 0 || h == null || h.waitStatus < 0 ||
 *             (h = head) == null || h.waitStatus < 0) {
 *             Node s = node.next;
 *             if (s == null || s.isShared())
 *                 doReleaseShared();
 *       	}
 *     }
 *
 *
 *     public void countDown() {
 *         sync.releaseShared(1); //直接调用的就是AQS的releaseShared
 *     }
 *     //CAS -1操作，释放所有等待的线程
 *     public final boolean releaseShared(int arg) {
 *         if (tryReleaseShared(arg)) {
 *             doReleaseShared();
 *             return true;
 *         }
 *         return false;
 *     }
 *
 */
public class CountDownLatchTest {

	public long timecost(final int times, final Runnable task) throws InterruptedException {
		if (times <= 0) {
			throw new IllegalArgumentException();
		}
		final CountDownLatch startLatch = new CountDownLatch(1);// 1.确保所有线程在开始执行之前，所有的准备工作已经完成
		final CountDownLatch overLatch = new CountDownLatch(times);
		for (int i = 0; i < times; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						/**
						 * 内部实现了if(tryAcquireShared(arg) < 0) doAcquireSharedInterruptibly(arg);
						 * 共享锁：线程共享一个资源，一旦一个资源拿到了锁，那么所有线程都拥有同一份资源。
						 * 通常情况下，共享锁只是一个标志，所有线程都等待这个标识是否满足，一旦满足所有线程都被激活。
						 * CountDownLatch就是基于共享锁的实现。
						 * public int tryAcquireShared(int acquires){
						 * 	return getState() == 0 ? 1 : -1;
						 * }
						 * 对于锁而言，第一次await时tryAcquireShared应该总是-1，因为对于CountDownLatch而言state的值就是初始化count的值。
						 * 此时执行，doAcquireSharedInterruptibly(arg);
						 * doAcquireSharedInterruptibly：
						 * 1.检查当前线程节点以共享模式加入AQS的CLH队列中，进行2
						 * 2.检查当前节点的前驱节点，如果是头节点并且闭锁计数为0就将当前节点设置为头节点。
						 * 	 唤醒继任节点，返回（结束线程阻塞）；否则进行3 --- setHeadAndPropagate(node,r)
						 * 3.检查当前线程是否应该阻塞，如果应该就阻塞（park）直到被唤醒（unpark）。重复2
						 * 4.如果2，3有异常就抛出（结束线程阻塞）
						 * 
						 */
						startLatch.await();
						task.run();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						overLatch.countDown();// 每个任务执行完成，计数器锁-1
					}
				}
			}).start();
		}
		long start = System.nanoTime();
		/**
		 * 条件满足时，唤醒头节点（时间最长的一个节点），然后根据FIFO队列唤醒整个节点列表
		 * 直接调用AQS的sync.releaseShared(1);
		 * tryReleaseShared采用CAS操作减少操作:
		 * public boolean tryReleaseShared(int releases){
		 * 		for(;;){
		 * 			int c = getState();
		 * 			if(c == 0)
		 * 				return false;
		 * 			int nextc = c - 1;
		 * 			if(compareAndSetState(c, nextc))
		 * 				return nextc == 0;
		 * 		}
		 * }
		 * 
		 */
		startLatch.countDown();// 2.所有准备工作完成，打开闭锁，所有线程开始执行
		overLatch.await();// 等待所有任务执行完成，当所有任务完成时，overLatch获取计数器值为0
		return System.nanoTime() - start;
	}

}
