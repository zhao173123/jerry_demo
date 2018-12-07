package com.sun.java8.reflect.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.google.common.collect.Maps;

/**
 * 反射
 * 
 * @author jerry
 *
 */
public class ReflectMainTest {

	private static HashMap<Class<?>, HashMap<Integer, String>> enumCacheMap = Maps.newHashMap();
	
	@Test
	public void testMethod() {
		Method[] methods = String.class.getMethods();
		for (int i = 0; i < methods.length; i++) {
			System.out.println(methods[i].getName());// 包括Object的方法也会打印出来
		}
	}

	@Test
	public void testArray() {
		int[] arr = (int[]) Array.newInstance(int.class, 3);
		Array.set(arr, 0, 111);
		Array.set(arr, 1, 222);
		Array.set(arr, 2, 333);
		System.out.println(Array.get(arr, 0));
		System.out.println(Array.get(arr, 1));
		System.out.println(Array.get(arr, 2));
	}

	/**
	 * result:
	 * 类名：jerry_test.reflect.ReflectEnum
	 * key:0, value:COMMON
	 * key:1, value:STANDAED
	 * key:2, value:SPECIAL
	 * 
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testEnum() throws ClassNotFoundException {
		Class<?> clazz = Class.forName("jerry_test.reflect.ReflectEnum");
		initEnum(clazz);
		for(Map.Entry<Class<?>, HashMap<Integer, String>> entry : enumCacheMap.entrySet()){
			Class<?> clz = entry.getKey();
			System.out.println("类名：" + clz.getName());
			HashMap<Integer, String> value = entry.getValue();
			for(Map.Entry<Integer, String> map : value.entrySet()){
				System.out.println("key:" + map.getKey() + ", value:" + map.getValue());
			}
		}
	}
	
	private void initEnum(Class<?> clazz){
		if (clazz.isEnum()) {
			HashMap<Integer, String> emap = new HashMap<Integer, String>();
			Method getVal = null;
			try {
				getVal = clazz.getMethod("val");
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
			if (getVal != null) {
				// 得到enum的所有实例
				Object[] objs = clazz.getEnumConstants();
				for (Object obj : objs) {
					try {
						//getVal.invoke(obj):得到index
						//obj.toString():得到value
						emap.put((Integer) getVal.invoke(obj), obj.toString());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			Map<Integer, String> sortMap = new TreeMap<Integer, String>(new Comparator<Integer>(){
				@Override
				public int compare(Integer str1, Integer str2) {
					return str1.compareTo(str2);  
				}
			});
			sortMap.putAll(emap);
			enumCacheMap.put(clazz, emap);
		}
	}
	
	/**
	 * isAssignableFrom
	 * 用来判断一个类Class1和另一个类Class2是否相同或是另一个类的子类或接口
	 */
	@Test
	public void testIsAssignableFrom(){
		//判断String是否是Object的父类
		boolean isParentClass = String.class.isAssignableFrom(Object.class);
		assertFalse(isParentClass);
		//判断Object是否是String的父类
		isParentClass = Object.class.isAssignableFrom(String.class);
		assertTrue(isParentClass);
		
		//判断Class1和Class2是否相同
		boolean isSameClass = String.class.isAssignableFrom(String.class);
		assertTrue(isSameClass);
	}

}

