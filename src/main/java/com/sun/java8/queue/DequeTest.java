package com.sun.java8.queue;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

/**
 * 1.queue是一种常用的数据结构，可以将队列看作一种特殊的线性表
 * 遵循先进先出原则
 * LinkedList实现了Queue接口，因此LinkedList的插入和删除效率较高
 * 	 1.1 boolean offer(E e):将元素追加到队列末尾，若添加成功返回true
 *   1.2 E poll():从对首删除并返回该元素
 *   1.3 E peek():返回对首元素，但是不删除
 * 
 * 2.双向队列Deque是Queue的一个子接口
 * 双向队列指该队列两端都可以入队(offer)和出对(poll|peek)，
 * 如果将Deque限制为只能从一端入队和出对，则可以实现栈的数据结构。
 * 对于栈而言，有入栈push和出栈pop，遵循先进先出	
 * 	 2.1 void push(E e): 给定元素压栈，存入的元素会在栈首
 * 	 2.2 E pop():弹出斩首元素并删除 
 * 
 * @author jerry
 *
 */
public class DequeTest {

	@Test
	public void testQueue(){
		Queue<String> queue = new LinkedList<String>();
		queue.offer("a1");
		queue.offer("a2");
		queue.offer("a3");
		System.out.println("initial queue : " + queue);
		queue.peek();
		System.out.println("after peek : " + queue);
		queue.poll();
		System.out.println("after poll : " + queue);
	}
	
	@Test
	public void testDequeue(){
		Deque<String> deque = new LinkedList<String>();
		deque.push("a1");
		deque.push("a2");
		deque.push("a3");
		System.out.println("initial deque : " + deque);
		deque.pop();
		System.out.println("after pop : " + deque);
	}
}
