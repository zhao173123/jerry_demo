package com.sun.java8.concurrent.locks;

import com.sun.java8.concurrent.atomic.Counter;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class LockCounter implements Counter{

	private long counter = 0l;
	//读写锁
	private WriteLock lock = new ReentrantReadWriteLock().writeLock();
	
	@Override
	public void increment() {
		lock.lock();
		counter++;
		lock.unlock();
	}

	@Override
	public long getCounter() {
		return counter;
	}

}
