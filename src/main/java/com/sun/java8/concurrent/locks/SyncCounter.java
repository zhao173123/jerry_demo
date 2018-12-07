package com.sun.java8.concurrent.locks;

import com.sun.java8.concurrent.atomic.Counter;

public class SyncCounter implements Counter{

	private long counter = 0l;
	
	@Override
	public synchronized void increment() {
		counter++;
	}

	@Override
	public long getCounter() {
		return counter;
	}

}
