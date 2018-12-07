package com.sun.java8.concurrent.locks;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 对应CountDownLatch为一次性闭锁， CycBarrier是可以重复使用的闭锁 允许一组线程相互等待，
 * 直到到达某个公共屏障点（common barrier point）， 所谓屏障点就是一组任务执行完毕的时刻。
 * 
 * await():线程挂起，直到同组的其他线程执行完毕才能执行
 * await():返回线程执行完毕的索引，索引时从任务数-1开始；也就是第一个执行完成的任务索引为parties-1，最后一个为0，
 * 这个parties为总任务数，清单中是cnt+1，如果屏障操作不依赖挂起的线程，那么任何线程都可以执行屏障操作。
 * 
 * API：
 * public CyclicBarrier(int parties) ：
 * 	它将在给定数量的参与者（线程）处于等待状态是启动。但不会在启动barrier时执行预定义的操作
 * public CyclicBarrier(int parties, Runnable barrierAction) ：
 * 	在给定数量(parties)的线程处于等待状态时启动，并在启动barrier时执行给定的屏障操作，该操作由最后一个进入barrier的线程执行;
 * 	barrierAction为CyclicBarrier接收的Runnable命令，用于在线程到达屏障时，优先执行barrierAction。
 * public int await() throws InterruptedException, BrokenBarrierException ：
 * 	在所有参与者都已经在此barrier上调用await方法之前，将一直等待
 * public int await(long timeout,TimeUnit unit) throws InterruptedException, BrokenBarrierException,TimeoutException ： 
 * 	在所有参与者都已经在此屏障上调用await方法之前或者超出了指定的等待时间，将一直等待
 * public int getNumberWaiting() ：返回当前在屏障处等待的参与者数目。此方法主要用于调试和断言
 * public int getParties() ：返回要求启动此 barrier 的参与者数目
 * public boolean isBroken() ：查询此屏障是否处于损坏状态
 * public void reset() ：将屏障重置为其初始状态
 * 
 * @author jerry
 *
 *	此实例不是很好，当一组任务执行完后才一次性按下面的顺序打印出： run over Left 50% Left 30%
 *	而不是一次性打出：Left 50% Left 30% run over 
 *	（run over先打印应该是：最后一个执行barrier.await()最先返回0，其他的还在唤醒中） 
 */
///................///
/**
 * 基本概念：如果要让线程等待其他线程执行完毕，那么已经执行完毕的线程（进入await()方法）就需要park(),直到超时或者被中断或者被其他线程唤醒
 * 源码分析：
 *
 * CyclicBarrier的特点就是要么线程都执行完毕，要么线程都异常被中断；不会有其中一个被中断而其他正常执行完毕的现象存在，这种叫all-or-none。
 * 而要完成这个特点，就必须有一个状态来描述曾经是否有过线程被中断（broken）了，这样后面执行完的线程就该知道是否需要继续等待了。
 * CyclicBarrier的Generation就是为了完成这件事的。Generation是CyclicBarrier内部类：定义是否发生了broken操作，
 * private static class Generation{
 * 		boolean broken = false;
 * }
 *
 * 由于有竞争资源（broken／index）的存在，所以需要一把锁（ReentrantLock）lock，拿到锁之后的操作
 * 1.检查是否存在中断位（broken），如果存在立即执行BrokenBarrierException返回。此异常描述的是线程进入屏障被破坏的状态。否则进行2
 * 2.检查当前线程是否被中断（Interrupted），如果是那么就设置中断位 “generation.broken = true”（使其他进入等待的线程知道），
 * 	 另外唤醒已经等待的线程“trip.signalAll()”，同时以InterruptedException返回，表示线程要中断。否则进行3
 * 3.将剩余任务-1 “int index = --count”，如果此时剩下的任务数为0，也就是达到了公共屏障点，那么就执行屏障点任务（如果有的话）；
 * 	 同时创建新的Generation->"new nextGeneration()"（在这个过程中会唤醒其他所有线程"trip.signalAll();"，
 *   因此当前线程是屏障点线程，那么其他线程就应该在等待状态）。否则执行4
 * 4.到这里说明还没有达到屏障点，那么此时线程就应该park()。很显然下面的for循环就是要park线程。这里park线程采用的是Condition.await()方法。
 *   也就是trip.await()。采用Condition是因为所有的await()其实等待的都是一个条件，一旦条件满足就应该被唤醒，所以Condition满足这个要求。
 *
 *     public int await() throws InterruptedException, BrokenBarrierException {
 *         try {
 *             return dowait(false, 0L);
 *         } catch (TimeoutException toe) {
 *             throw new Error(toe); // cannot happen
 *         }
 *     }
 *
 *     private int dowait(boolean timed, long nanos)
 *         throws InterruptedException, BrokenBarrierException,
 *                TimeoutException {
 *         final ReentrantLock lock = this.lock;
 *         lock.lock();
 *         try {
 *             final Generation g = generation;
 *			   //当前generation已经损坏，一般都是因为某个线程在等待处于“断开”状态的CyclicBarrier
 *             if (g.broken)
 *                 throw new BrokenBarrierException();
 *             //如果线程中断，终止CyclicBarrier
 *             if (Thread.interrupted()) {
 *                 breakBarrier();
 *                 throw new InterruptedException();
 *             }
 *             // 进来一个线程，count-1
 *             int index = --count;
 *             if (index == 0) {  // 表示所有线程均已到位，触发Runnable任务
 *                 boolean ranAction = false;
 *                 try {
 *                     final Runnable command = barrierCommand;
 *                     if (command != null) //如果有触发任务则执行
 *                         command.run();
 *                     ranAction = true;
 *                     nextGeneration(); //唤醒所有等待线程线程，并更新generation
 *                     return 0;
 *                 } finally {
 *                     if (!ranAction)
 *                         breakBarrier();
 *                 }
 *             }
 *
 *             // loop until tripped, broken, interrupted, or timed out
 *             for (;;) {
 *                 try {
 *                     if (!timed) // 不是超时等待，调用Condition.await
 *                         trip.await();
 *                     else if (nanos > 0L) // 超时等待，调用Condition.awaitNanos
 *                         nanos = trip.awaitNanos(nanos);
 *                 } catch (InterruptedException ie) {
 *                     if (g == generation && ! g.broken) {
 *                         breakBarrier();
 *                         throw ie;
 *                     } else {
 *                         // We're about to finish waiting even if we had not
 *                         // been interrupted, so this interrupt is deemed to
 *                         // "belong" to subsequent execution.
 *                         Thread.currentThread().interrupt();
 *                     }
 *                 }
 *
 *                 if (g.broken)
 *                     throw new BrokenBarrierException();
 *
 *                 if (g != generation)
 *                     return index;
 *
 *                 if (timed && nanos <= 0L) {
 *                     breakBarrier();
 *                     throw new TimeoutException();
 *                 }
 *             }
 *         } finally {
 *             lock.unlock();
 *         }
 *     }
 *
 */
public class CyclicBarrierTest {

	final CyclicBarrier barrier;
	final int MAX_TASK;

	public CyclicBarrierTest(int cnt) {
		barrier = new CyclicBarrier(cnt + 1);
		MAX_TASK = cnt;
	}

	public void doWork(final Runnable work) {

		new Thread(() -> {
			work.run();
			try {
				int index = barrier.await();
				doWithIndex(index);
			} catch (InterruptedException e) {
				return;
			} catch (BrokenBarrierException e) {
				return;
			} finally {

			}
		}).start();
	}

	protected void doWithIndex(int index) {
		if (index == MAX_TASK / 3) {
			System.out.println("Left 30%");
		} else if (index == MAX_TASK / 2) {
			System.out.println("Left 50%");
		} else if (index == 0) {
			System.out.println("run over!");
		}
	}

	public void waitForNext() {
		try {
			doWithIndex(barrier.await());
		} catch (InterruptedException e) {
			return;
		} catch (BrokenBarrierException e) {
			return;
		}
	}

	/**
	 * 周期性任务处理
	 * 100个任务，每10个一起进行处理；
	 * 当且仅当上一组任务处理完成后才进行下一组；
	 * 在一组任务中，当任务还剩下50%，30%，0%时执行特殊任务（发通知打印）
	 * @param args
	 */
	public static void main(String[] args) {
		final int count = 10;
		CyclicBarrierTest test = new CyclicBarrierTest(count);
		for (int i = 0; i < 100; i++) {
			test.doWork(() -> {
				try {
					Thread.sleep(1000L);
				} catch (Exception e) {
					return;
				}
			});
			if ((i + 1) % count == 0) {
				test.waitForNext();
			}
		}
	}
}
