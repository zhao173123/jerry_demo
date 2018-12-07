package com.sun.java8.concurrent.atomic.unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
/**
 * Unsafe 可以有意执行一些不安全的、容易犯错的方法
 * 比较通用的得到Unsafe类的做法：
 * 1. MyUnsafe.java
 * Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
 * f.setAccessible(true);
 * Unsafe unsafe = (Unsafe)f.get(null);
 * 2.
 * 还可以在运行程序时，使用bootclasspath选项，指定系统类路径上加上一个自己使用的unsafe
 * java -Xbootclasspath:/usr/jdk1.7.0/jre/lib/rt.jar:. com.mishadoff.magic.UnsafeClient
 * 
 * Unsafe API比较通用的一些接口：
 * 1.Info返回一些低级的内存信息
 * 	·addressSize
 * 	·pageSize
 * 2.Objects 提供用于操作对象及其字段的方法
 * 	·allocateInstance
 * 	·objectFieldOffset
 * 3.Classes 提供用于操作类及其静态字段的方法
 * 	·staticFieldOffset
 * 	·defineClass
 * 	·defineAnonymousClass
 * 	·ensureClassInitialized
 * 4.Arrays 操作数组
 * 	·arrayBaseOffset
 * 	·arrayIndexScale
 * 5.Synchronization低级的同步原语
 * 	·monitorEnter
 * 	·tryMonitorEnter
 * 	·monitorExit
 * 	·compareAndSwapInt
 * 	·putOrderedInt
 * 6.Memory直接访问内存的方法
 * 	·allocateMemory
 * 	·copyMemory
 * 	·freeMemory
 * 	·getAddress
 * 	·getInt
 *  ·putInt
 * @author jerry
 *
 */
public class Guard {

	private int ACCESS_ALLOWED = 1;
	
	public boolean getAccess(){
		return 42 == ACCESS_ALLOWED;
	}
	
	@SuppressWarnings({ "restriction", "unchecked" })
	public static <Unsafe> void main(String[] args) throws NoSuchFieldException, 
		SecurityException, IllegalArgumentException, IllegalAccessException, IOException{
		Guard guard = new Guard();
		boolean access = guard.getAccess();
		System.out.println(access);//false
		//获取UnSafe
		Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		Unsafe unsafe = (Unsafe) f.get(null);
//		sun.misc.Unsafe unsafe = sun.misc.Unsafe.getUnsafe();
		
		Field field = guard.getClass().getDeclaredField("ACCESS_ALLOWED");
		Long l1 = ((sun.misc.Unsafe) unsafe).objectFieldOffset(field);
		System.out.println("l1 : " + l1);//12
		((sun.misc.Unsafe) unsafe).putInt(guard, l1, 42);//修改内存的guard.ACCESS_ALLOWED
		access = guard.getAccess();
		/*
		 * 一般情况下，access应该都是返回false
		 * 但是这里，我们通过UnSafe绕过了安全检查器直接修改了内存中Guard.ACCESS_ALLOWED
		 * 所以这里返回true
		 */
		System.out.println(access);//true
		
		System.out.println(sizeOf(new Integer(1)));//16
		
		byte[] content = getClassContent();
		System.out.println("content : " + content);
		// int[].class index : 4
		System.out.println("int[].class index : " + MyUnsafe.getUnsafe().arrayIndexScale(int[].class));
		// long[].class index : 8
		System.out.println("long[].class index : " + MyUnsafe.getUnsafe().arrayIndexScale(long[].class));
		
		//创建假的string对象，取代内存中原来的string
		String password = new String("100k@myHor$e");
		String fake = new String(password.replaceAll(".", "?"));
		System.out.println(password);
		System.out.println(fake);
		MyUnsafe.getUnsafe().copyMemory(fake, 0L, null, toAddress(password), sizeOf(password));
		System.out.println(password);
		System.out.println(fake);

		int a = MyUnsafe.getUnsafe().getInt(1);
		System.out.println("unsafe.getInt() : " + a);
	}
	
	/**
	 * 通过结合java反射和objectFieldOffset实现C like style的sizeOf()函数
	 * 返回对象的自身内存大小
	 * 算法如下：
	 * 从底层子类开始，依次取出它自己和它所有超类的非静态域，放置到一个HashSet中，
	 * 重复的只计算一次，然后使用objectFieldOffset()获取一个最大偏移量，最后考虑对齐
	 */
	@SuppressWarnings({"restriction", "rawtypes", "unchecked"})
	public static <Unsafe> long sizeOf(Object o) throws NoSuchFieldException, 
		SecurityException, IllegalArgumentException, IllegalAccessException{
//		Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
//		f.setAccessible(true);
//		Unsafe unsafe = (Unsafe) f.get(null);
		
		Unsafe unsafe = (Unsafe) MyUnsafe.getUnsafe();
		HashSet<Field> fields = new HashSet<>();
		Class c = o.getClass();
		while(c != Object.class){
			for(Field field : c.getDeclaredFields()){
				if((field.getModifiers() & Modifier.STATIC) == 0){
					fields.add(field);
				}
			}
			c = c.getSuperclass();
		}
		
		//get offset
		long maxSize = 0;
		for(Field field : fields){
			long offset = ((sun.misc.Unsafe) unsafe).objectFieldOffset(field);
			if(offset > maxSize){
				maxSize = offset;
			}
		}
		// padding
		return ((maxSize/8) + 1) * 8;
	}
	
	/**
	 * 仅读取类结构的大小值
	 * JVM1.7 32bit 中偏移量12
	 * @param o
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@SuppressWarnings("restriction")
	public static long sizeOfClass(Object o) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		sun.misc.Unsafe unsafe = (sun.misc.Unsafe)MyUnsafe.getUnsafe();
		return unsafe.getAddress(normalize(unsafe.getInt(o, 4L)) + 12L);
	}
	
	/**
	 * 浅拷贝
	 * 用来拷贝任何类型的对象，动态计算它的大小
	 * 在拷贝后，你需要将对象转换成特定的类型
	 * @param obj
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@SuppressWarnings("restriction")
	public static Object shallowCopy(Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		long size = sizeOf(obj);
		long start = toAddress(obj);
		long address = MyUnsafe.getUnsafe().allocateMemory(size);
		MyUnsafe.getUnsafe().copyMemory(start, address, size);
		return fromAddress(address);
	}
	
	 //将对象转换为其在内存中的地址
	@SuppressWarnings("restriction")
	public static long toAddress(Object o) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Object[] array = new Object[]{o};
		long baseOffset = MyUnsafe.getUnsafe().arrayBaseOffset(Object[].class);
		return normalize(MyUnsafe.getUnsafe().getInt(array, baseOffset));
	}
	
	 //将其在内存中的地址转换为对象
	@SuppressWarnings("restriction")
	static Object fromAddress(long address) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Object[] array = new Object[] {null};
		long baseOffset = MyUnsafe.getUnsafe().arrayBaseOffset(Object[].class);
		MyUnsafe.getUnsafe().putLong(array, baseOffset, address);
		return array[0];
	}
	
	/**
	 * 为了正确内存地址使用，将有符号的int类型强转成无符号的long类型
	 * 
	 * @param value
	 * @return
	 */
	private static long normalize(int value){
		if(value >= 0){
			return value;
		}
		return (~0 >>> 32) & value;
	}
	

	/**
	 * 封装获取Unsafe的方法
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private static <Unsafe> sun.misc.Unsafe getUnsafe() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		return (sun.misc.Unsafe) f.get(null);
	}
	
	//将此段代码添加到超类中，就可以强制转换且无异常 ： (String) (Object)(new Integer(666))
	public void forceConvert() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		long intClassAddress = normalize(MyUnsafe.getUnsafe().getInt(new Integer(0), 4L));
		long strClassAddress = normalize(MyUnsafe.getUnsafe().getInt("", 4L));
		MyUnsafe.getUnsafe().putAddress(intClassAddress + 36, strClassAddress);
	}
	
	//动态类：创建一个类
	public void createDynamicClass() throws NoSuchFieldException, SecurityException, 
				IllegalArgumentException, IllegalAccessException, InvocationTargetException, 
					NoSuchMethodException, InstantiationException, IOException{
		//从一个已编译的.class文件,将类内容读取为字节数组，并正确传递给defineClass方法
		byte[] classContents = getClassContents();
		Class c = MyUnsafe.getUnsafe().defineClass(null, classContents, 0, classContents.length, Guard.class.getClassLoader(), null);
		c.getMethod("a").invoke(c.newInstance(), null);//1
	}
	
	//从定义的class文件读取代码
	private static byte[] getClassContents() throws IOException{
		File f = new File("/home/.../..../A.class");
		FileInputStream input = new FileInputStream(f);
		byte[] content = new byte[(int)f.length()];
		input.read(content);
		input.close();
		return content;
	}
	
	/**
	 * 将一个class文件读取到一个byte[]数组
	 * @return
	 * @throws IOException 
	 */
	private static byte[] getClassContent() throws IOException{
		File f = new File("/Users/jerry/myself/github/jerry_test/target/classes/com/sun/java8/concurrent/atomic/unsafe/Guard.class");
		FileInputStream input = new FileInputStream(f);
		byte[] content = new byte[(int)f.length()];
		input.read(content);
		input.close();
		return content;
	}
	
	/**
	 * 标准的java序列化能力非常慢，要求类必须有一个公共的、无参数的构造器
	 * Externalizable比较好，但它需要定义序列化的模式
	 * 
	 * unsafe类可以很容易实现完整的序列化周期
	 */
	
	
}
