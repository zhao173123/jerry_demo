package com.sun.java8.concurrent.atomic.base;

import java.util.concurrent.atomic.AtomicReference;


public class AtomicReferenceTest {

	public static final AtomicReference<User> atomicUserRef = new AtomicReference<User>();
	
	public static void main(String[] args){
		User user = new User("jerry", 30);
		atomicUserRef.set(user);
		User updaterUser = new User("tom", 23);
		atomicUserRef.compareAndSet(user, updaterUser);
		System.out.println(atomicUserRef.get().getName());//tom
		System.out.println(atomicUserRef.get().getAge());//23
	}

}

class User {
	private String name;
	private int age;
	
	public User(String name, int age){
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
