package com.sun.java8.queue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * LinkedBlockingQueue的原理和使用场景
 * 
 * 1.LinkedBlockingQueue源码：
 * 	1.1 使用2个Node来存放首尾节点，Node<E>：last／Node<E>：head
 *  1.2 AtomicInteger count = new AtomicInteger() 来记录队列元素个数
 *  1.3 2个ReentrantLock的独占锁，分别控制元素入队(putLock)和出列加锁(takeLock)
 * 2.使用分析
 * 
 * @author jerry
 *
 */
public class LinkedBlockingQueueTest {

	public static void main(String[] args){
		LinkedBlockingQueue queue = new LinkedBlockingQueue();
	}
	
	
}
