package com.sun.java8.concurrent.locks;

import com.sun.java8.concurrent.atomic.AtomicCounter;
import com.sun.java8.concurrent.atomic.Counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author jerry
 *
 */
public class CounterClientTest {

	public static void main(String[] args) throws Exception {
		int NUM_OF_THREADS = 1000;
		int NUM_OF_INCREMENTS = 100000;
		ExecutorService exec = Executors.newFixedThreadPool(NUM_OF_THREADS);
//		Counter counter = new StupidCounter();//结果不准确，没有锁控制，Counter result:99612438 Time passed in ms :358
//		Counter counter = new SyncCounter();//synchronized控制，Counter result:100000000 Time passed in ms :3240
//		Counter counter = new LockCounter();//准确，Time passed in ms :3435
		Counter counter = new AtomicCounter();//使用Atomic，Time passed in ms :2143
//		Counter counter = new CASCounter();//使用CAS控制，Time passed in ms :8498
		long before = System.currentTimeMillis();
		
		for (int i = 0; i < NUM_OF_THREADS; i++) {
			exec.submit(new CounterClient(counter, NUM_OF_INCREMENTS));
		}
		exec.shutdown();
		exec.awaitTermination(1, TimeUnit.MINUTES);
		long after = System.currentTimeMillis();
		System.out.println("Counter result:" + counter.getCounter());
		System.out.println("Time passed in ms :" + (after - before));
	}
}
