package com.sun.java8.concurrent.atomic.base;

import lombok.Data;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdaterTest {

	class DemoData{
		public volatile int value1 = 1;
	}
	
	AtomicIntegerFieldUpdater<DemoData> getUpdater(String fieldName){
		return AtomicIntegerFieldUpdater.newUpdater(DemoData.class, fieldName);
	}
	
	void doinit(){
		DemoData data = new DemoData();
		System.out.println("1=====>" + getUpdater("value1").getAndSet(data, 10));//1=====>1
		System.out.println(data.value1);//10
		System.out.println("2=====>" + getUpdater("value2").incrementAndGet(data));//2=====>3
		//value3,value4对AtomicIntegerFieldUpdaterTest是不可见的（protected、private）
		//所有不能直接通过反射修改值
		System.out.println("3=====>" + getUpdater("value3").decrementAndGet(data));
		System.out.println("true====>" + getUpdater("value4").compareAndSet(data, 4, 5));
	}
	
	public static void main(String[] args){
		AtomicIntegerFieldUpdaterTest test = new AtomicIntegerFieldUpdaterTest();
		test.doinit();
	}

	/**
	 * 原子更新字段类
	 * 1.每次使用必须使用静态方法newUpdater创建更新器，并且需要设置项要更新的类和属性
	 * 2.更新类的字段（属性)必须使用public volatile
	 */
	@Test
	public void testAtomicIntegerFieldUpdater(){
		AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater.newUpdater(User.class, "old");

		User conan = new User("conan", 10);
		System.out.println(a.getAndIncrement(conan));
		System.out.println(a.get(conan));
	}

	@Data
	public static class User{
		private String name;
		public volatile int old;

		public User(String name, int old){
			this.name = name;
			this.old = old;
		}
	}
}
