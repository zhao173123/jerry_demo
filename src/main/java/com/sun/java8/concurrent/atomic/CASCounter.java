package com.sun.java8.concurrent.atomic;

import com.sun.java8.concurrent.atomic.unsafe.MyUnsafe;

public class CASCounter implements Counter{

	private volatile long counter = 0;
	private sun.misc.Unsafe unsafe;
	private long offset;
	
	public CASCounter() throws Exception {
		unsafe = MyUnsafe.getUnsafe();
		offset = unsafe.objectFieldOffset(CASCounter.class.getDeclaredField("counter"));
	}
	
	@Override
	public void increment() {
		long before = counter;
		while(!unsafe.compareAndSwapLong(this, offset, before, before + 1)){
			before = counter;
		}
	}

	@Override
	public long getCounter() {
		return counter;
	}

}
