package com.sun.java8.reflect.type;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Maps;
import com.sun.java8.reflect.vo.TypeVO;

public class ParameterTest {

	public static void main(String[] args){
		TypeVO vo = new TypeVO();
		vo.setPp(0);
		System.out.println(vo.getPp() + "," + TypeVO.Type.bb.ordinal());
		System.out.println(vo.getPp() == (TypeVO.Type.aa.ordinal()));
		
		Long l1 = new Long(212);
		long l2 = 21;
		System.out.println(Long.compare(l1, l2));
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("a1", null);
		System.out.println(map);
		
		BigDecimal total = new BigDecimal("0.00");
		System.out.println(total);//0.00
		
		System.out.println(testAssert(3));//true
		System.out.println(testAssert(1));//true
		int a =3,b =4;
		System.out.println("a:" + (a = b =3));//3
		System.out.println(b);//3
		
		ConcurrentHashMap<String, String> chm = new ConcurrentHashMap<String, String>();
		chm.put("a", "1");
		chm.put("b", "2");
		for(Entry<String, String> entry : chm.entrySet()){
			System.out.println("key:" + entry.getKey());
			System.out.println("value:" + entry.getValue());
		}
		
		int t10 = 1;
		int t20 = t10, p = t20;
		//Node<E> t = tail, p = t;;
		//Node<E> q = p.next;
		//p = (p != t && t != (t = tail)) ? t : q;
		p = (p != t20 && t20 != (t20 = t10)) ? t20 : p;
		System.out.println(p);//1
		
		Long aa = 123l;
		if(aa != null){
			System.out.println(aa);
		}
		
//		for(int a10 = 10, c10 = a10;;){
//			int b10 = c10;
//			System.out.println(b10);
////			c10 = 20;//之后都打印20
//			a10 = 5;//不起作用的
//			System.out.println(b10);
//			
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			
//		}
		int result = testCatch();
		System.out.println(result);
	}
	
	public static boolean testAssert(int a){
		assert a == 1;//断言，默认关闭的
		return true;
	}
	
	public static int testCatch(){
		try{
			System.out.println(1/0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return 100;
	}
}
