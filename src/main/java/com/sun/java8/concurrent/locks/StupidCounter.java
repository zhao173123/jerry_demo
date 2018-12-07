package com.sun.java8.concurrent.locks;

import com.sun.java8.concurrent.atomic.Counter;

public class StupidCounter implements Counter{

	private long counter = 0;
	
	@Override
	public void increment() {
		counter++;
	}

	@Override
	public long getCounter() {
		return counter;
	}

}
