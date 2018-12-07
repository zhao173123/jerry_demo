package com.sun.java8.lamdbaInternal;

import org.junit.Test;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Lamdba的一个重要作用就是简化某些匿名内部类的写法， Lamdba不能取代所有的内部类实现，只是支持Functional
 * Interface（函数式接口）的简写
 * 
 * 1.通过javap -c xxx.class 分析字节码，可以看出JDK7的匿名内部类实现方式会创建新的对象（class）
 *   而lamdba的写法只有一个类，不会新建新的匿名内部类class
 * 2.通过javap -c -p xxx.class 可以看出Lamdba被封装成主类的一个私有方法，
 * 	 并通过invokedynamic指令进行调用
 * 
 * @author jerry
 */
public class AnonymousTest {

	// 新建一个线程
	@Test
	public void testThread() {
		// JDK7
		new Thread(() -> System.out.println("JDK7 Thread run()...")).start();
		// JDK8，接口名和函数名一起省略
		new Thread(() -> {
			System.out.println("JDK8 Thread run()...");
		}).start();
	}

	// 带参函数的简写
	// 给定一个字符串列表，通过自定义比较器，按照字符串长度进行排序
	@Test
	public void testParam() {
		List<String> list = Arrays.asList("I", "love", "you", "too");
		// JDK7
		Collections.sort(list, new Comparator<String>() {// 接口名
			@Override
			public int compare(String s1, String s2) {// 方法名
				if (s1 == null) {
					return -1;
				}
				if (s2 == null) {
					return 1;
				}
				return s1.length() - s2.length();
			}
		});

		// JDK8
		Collections.sort(list, (s1, s2) -> {// 省略参数的类型
			if (s1 == null) {
				return -1;
			}
			if (s2 == null) {
				return 1;
			}
			return s1.length() - s2.length();
		});
		// more
		Runnable run = () -> System.out.println("Hello Thread!");// 无参函数简写
		ActionListener listener = event -> System.out.println("Hello AWT!");// 有参函数简写
		Runnable multiLine = () -> {// 代码块简写
			System.out.println("Hello");
			System.out.println("mutiLine");
		};
		BinaryOperator<Long> add = (Long x, Long y) -> x + y;// 类型推断机制
		BinaryOperator<Long> addImlicit = (x, y) -> x + y;// 类型推断机制
	}

}
