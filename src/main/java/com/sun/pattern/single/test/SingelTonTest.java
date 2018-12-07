package com.sun.pattern.single.test;

import com.sun.pattern.single.ClassFactory;
import com.sun.pattern.single.EnumFactory;
import com.sun.pattern.single.SingleTon;
import com.sun.pattern.single.SingleTonStatic;

public class SingelTonTest extends Thread {

	@Override
	public void run() {
//		System.out.println(SingleTon.getInstance0());//非线程安全
//		System.out.println(SingleTon.getInstance1());//偶尔会有报错
//		System.out.println(SingleTon.getInstanceOk());
//		System.out.println(SingleTon.getInstanceok2());
//		System.out.println(SingleTon.getInstance3());//静态类实现
//		System.out.println(SingleTonStatic.getInstance());//static代码块实现
//		System.out.println(EnumFactory.singleTonFactory.getInstance());//枚举实现单列，此方法不好，枚举暴漏
		System.out.println(ClassFactory.getInstance2());//不暴露枚举类型的单列模式
	}

	public static void main(String[] args) {
		SingelTonTest[] singleTon = new SingelTonTest[10];
		for (int i = 0; i < singleTon.length; i++) {
			singleTon[i] = new SingelTonTest();
		}
		for (int j = 0; j < singleTon.length; j++) {
			singleTon[j].start();
		}
	}

}
