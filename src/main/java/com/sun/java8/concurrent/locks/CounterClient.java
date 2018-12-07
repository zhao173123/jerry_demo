package com.sun.java8.concurrent.locks;

import com.sun.java8.concurrent.atomic.Counter;

//Counter的工作线程
public class CounterClient implements Runnable{

	private Counter c;
	private int num;
	
	public CounterClient(Counter c, int num){
		this.c = c;
		this.num = num;
	}
	
	@Override
	public void run(){
		for(int i = 0; i < num; i++){
			c.increment();
		}
	}
}
