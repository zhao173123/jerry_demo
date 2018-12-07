package com.sun.java8.concurrent.atomic.unsafe;

import java.lang.reflect.Field;

/**
 * 获取自己的Unsafe
 * @author jerry
 *
 */
public class MyUnsafe {

	public static sun.misc.Unsafe getUnsafe() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		field.setAccessible(true);
		return (sun.misc.Unsafe)field.get(null);
	}
}
