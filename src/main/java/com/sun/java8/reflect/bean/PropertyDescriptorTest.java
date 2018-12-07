package com.sun.java8.reflect.bean;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import com.sun.java8.reflect.vo.PersonType;

/**
 * PropertyDescriotor表示JavaBean类通过存储器导出一个属性
 * 1.getReadMethod():获取用于读取属性值的方法
 * 2.getWriteMethod():获取用于写入属性值的方法
 * 
 * @author jerry
 *
 */
public class PropertyDescriptorTest {

	public static void main(String[] args)
			throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		PersonType p = new PersonType();
		p.setId("0");
		
		PropertyDescriptor prop = new PropertyDescriptor("id", PersonType.class);
		//获取getter方法，反射获取id值
		Object str = prop.getReadMethod().invoke(p);
		//获取setter方法，反射赋值
		prop.getWriteMethod().invoke(p, "1");
		
		System.out.println("获取Id：" + str);//0
		System.out.println("赋值Id：" + p.getId());//1
	}
}
