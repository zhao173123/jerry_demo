package com.sun.java8.concurrent.locks.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Semaphore:信号量
 * 可以控制某个资源被同时访问的个数
 * 通过acquire()获取一个许可，如果没有就等待；release()释放一个许可。
 * 单个信号量的Semaphore可以实现互斥锁功能
 *
 *  构造器说明，默认使用非公平锁：
 *  Semaphore(int permits):创建具有给定的许可数和非公平的公平设置的 Semaphore
 *  	sync = new NonfairSync(permits);
 *  Semaphore(int permits, boolean fair):创建具有给定的许可数和给定的公平设置的 Semaphore
 *  	 sync = fair ? new FairSync(permits) : new NonfairSync(permits);
 *  可以看出当permits=1的时候就是一个互斥锁，其中0、1就相当于它的状态，=1时表示其他线程可以获取；
 *  =0时表示其他线程必须等待。
 *  主要方法：
 *  1. public void acquire() throws InterruptedException {
 *         sync.acquireSharedInterruptibly(1);
 *     }
 *     //使用AQS的实现
 *     public final void acquireSharedInterruptibly(int arg)
 *             throws InterruptedException {
 *         if (Thread.interrupted())
 *             throw new InterruptedException();
 *         if (tryAcquireShared(arg) < 0)
 *             doAcquireSharedInterruptibly(arg);
 *     }
 *     Semaphore实现tryAcquireShared：
 *     公平锁：
 *	   protected int tryAcquireShared(int acquires) {
 *         for (;;) {
 *             //判断该线程是否位于CLH队列的列头
 *             if (hasQueuedPredecessors())
 *                 return -1;
 *             //获取当前的信号量许可
 *             int available = getState();
 *
 *             //设置“获得acquires个信号量许可之后，剩余的信号量许可数”
 *             int remaining = available - acquires;
 *
 *             //CAS设置信号量
 *             if (remaining < 0 ||
 *                     compareAndSetState(available, remaining))
 *                 return remaining;
 *         }
 *     }
 *
 *     非公平锁：
 *     protected int tryAcquireShared(int acquires) {
 *             return nonfairTryAcquireShared(acquires);
 *     }
 *
 * 	   final int nonfairTryAcquireShared(int acquires) {
 *             for (;;) {
 *                 int available = getState();
 *                 int remaining = available - acquires;
 *                 if (remaining < 0 ||
 *                     compareAndSetState(available, remaining))
 *                     return remaining;
 *             }
 *      }
 *
 *
 *  2. public void release() {
 *         sync.releaseShared(1);
 *     }
 *	   //调用AQS内部的实现
 *     public final boolean releaseShared(int arg) {
 *         if (tryReleaseShared(arg)) {
 *             doReleaseShared();
 *             return true;
 *         }
 *         return false;
 *     }
 *     //Semaphore.sync实现
 *     protected final boolean tryReleaseShared(int releases) {
 *         for (;;) {
 *             int current = getState();
 *             //信号量的许可数 = 当前信号许可数 + 待释放的信号许可数
 *             int next = current + releases;
 *             if (next < current) // overflow
 *                 throw new Error("Maximum permit count exceeded");
 *             //设置可获取的信号许可数为next
 *             if (compareAndSetState(current, next))
 *                 return true;
 *         }
 *     }
 *
 *
 *  实例说明：停车场有5个停车位，先后来了3辆车，停车位足够，安排停车；
 *  又来了3辆车，停车位不足只能停2辆，剩余一辆等待；
 *  当停车场有车开走，则安排一辆等待的车进位（至于是哪辆车取决于是公平还是非公平锁了）。
 *
 * @author jerry
 *
 */
public class SemaphoreTest {

	static class Parking{
		private Semaphore semaphore;

		Parking(int count){
			semaphore = new Semaphore(count);
		}

		public void park(){
			try {
				semaphore.acquire();
				long time = (long) (Math.random() * 10);
				System.out.println(Thread.currentThread().getName() + "进入停车场，停车" + time + "秒");
				Thread.sleep(time);
				System.out.println(Thread.currentThread().getName() + "开出停车场...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				semaphore.release();
			}
		}
	}

	static class Car extends Thread{

		Parking parking;

		Car(Parking parking){
			this.parking = parking;
		}

		@Override
		public void run(){
			parking.park();
		}
	}

	public static void main(String[] args) {
		Parking parking = new Parking(3);
		for(int i = 0; i < 5; i++) {
			new Car(parking).start();
		}
	}

}
