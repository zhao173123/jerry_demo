package com.sun.pattern.single;

//使用static代码块实现单列模式
public class SingleTonStatic {

	private static SingleTonStatic instance = null; 
	
	private SingleTonStatic(){}
	
	static {
		instance = new SingleTonStatic();
	}
	
	public static SingleTonStatic getInstance(){
		return instance;
	}
}

