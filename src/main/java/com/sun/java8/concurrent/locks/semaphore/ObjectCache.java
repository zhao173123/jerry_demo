package com.sun.java8.concurrent.locks.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对象池实现 Semaphore
 * 
 * 一个基于信号量Semaphore的对象池实现
 * 此对象池最多支持capacity个对象
 * 对象池有一个基于FIFO的队列，每次从对象池的头结点开始取对象，
 * 如果头结点为空就直接构造一个新的对象返回;否则将头结点对象取出，并且头结点往后移动。
 * 特别要说明的如果对象的个数用完了，那么新的线程将被阻塞，直到有对象被返回回来。
 * 返还对象时将对象加入FIFO的尾节点并且释放一个空闲的信号量，表示对象池中增加一个可用对象。
 *
 * @author jerry
 *
 * @param <T>
 */
public class ObjectCache<T> {

	public interface ObjectFactory<T> {
		T makeObject();
	}

	class Node {
		T obj;
		Node next;
	}

	final int capacity;
	final ObjectFactory<T> factory;
	private final Lock lock = new ReentrantLock();
	private final Semaphore semaphore;
	private Node head;
	private Node tail;

	public ObjectCache(int capacity, ObjectFactory<T> factory) {
		this.capacity = capacity;
		this.factory = factory;
		this.semaphore = new Semaphore(this.capacity);
		this.head = null;
		this.tail = null;
	}

	public T getObject() throws InterruptedException {
		semaphore.acquire();//获取许可。如果没有就等待
		return getNextObject();
	}

	private T getNextObject() {
		lock.lock();
		try {
			if (head == null) {
				return factory.makeObject();//头节点为空，构造一个新的object
			} else {//将头结点对象取出->ret，并且头结点往后移动
				Node ret = head;
				head = head.next;
				if (head == null)
					tail = null;
				ret.next = null;// help gc
				return ret.obj;
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void returnObject(T t){
		returnObjectToPool(t);
		semaphore.release();//释放许可
	}
	
	private void returnObjectToPool(T t){
		lock.lock();
		try{
			Node node = new Node();
			node.obj = t;
			if(tail == null){
				head = tail = node;
			}else{
				tail.next = node;
				tail = node;
			}
		}finally{
			lock.unlock();
		}
	}
}
