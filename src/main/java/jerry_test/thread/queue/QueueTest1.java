package jerry_test.thread.queue;

import java.util.ArrayDeque;
import java.util.Queue;

import com.google.common.collect.Lists;

public class QueueTest1 {

	public static void main(String[] args){
//		testQueue();
		testArrayDeque();
	}
	
	private static void testArrayDeque(){
		ArrayDeque ad = new ArrayDeque();
		ad.offer("a");
		ad.offerFirst("b");
		ad.offerFirst("c");
		System.out.println(ad.pollFirst());//c
		
	}

	private static void testQueue() {
		Queue<String> queue = Lists.newLinkedList();
		//add element
		queue.offer("a");
		queue.offer("b");
		queue.offer("c");
		queue.offer("d");
		
		queue.forEach(q->{System.out.print(q + ",");});
		System.out.println("======");
		
		System.out.println("queue.poll():" + queue.poll());
		queue.forEach(q->{System.out.print(q + ",");});
		System.out.println("======");
		
		System.out.println("queue.element():" + queue.element());
		queue.forEach(q->{System.out.print(q + ",");});
		System.out.println("======");
		
		System.out.println("peek="+queue.peek()); //返回第一个元素
		queue.forEach(q->{System.out.print(q + ",");});
		System.out.println("======");
	}
}
