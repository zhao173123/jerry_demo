package com.sun.java8.concurrent.locks.aqs;

import com.sun.java8.concurrent.locks.aqs.Mutex;

import java.util.Collection;
import java.util.Random;

/**
 * 独占锁Mutex的测试
 * 
 * @author jerry
 *
 */
public class MutexTest {

	private static Random r = new Random(47);
	private static int threadCount = 10;
	private static Mutex mut = new Mutex();

	private static class Weight implements Runnable {// 给苹果称重的任务
		String name;

		public Weight(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			
			try{
				mut.lock();
				System.out.println(name + "放苹果！");
				System.out.println(name + "重量：" + (r.nextInt(10) + 3));
				System.out.println(name + "取苹果！");
				printQueueThreads(mut.getQueueThreads());
			}finally{
				mut.unlock();
			}
		}
	}
	
	private static void printQueueThreads(Collection<Thread> threads){
		System.out.print("等待队列中的线程:");
		for(Thread t : threads){
			System.out.print(t.getName() + ",");
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		Thread[] threads = new Thread[threadCount];
		for(int i = 0; i < threads.length; i++){
			threads[i] = new Thread(new Weight("Weight-" + i), "Thread-"+i);
		}
		for(int i = 0; i < threads.length; i++){
			threads[i].start();
		}
	}
}
