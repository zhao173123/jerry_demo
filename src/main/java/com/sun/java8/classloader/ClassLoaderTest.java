package com.sun.java8.classloader;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class ClassLoaderTest {

	@Test
	public void testWebUrl() throws MalformedURLException{
		URL url = new URL("http://www.runoob.com/index.html?language=cn#j2me");
		assertEquals("http", url.getProtocol());
		assertEquals("www.runoob.com", url.getAuthority());
		assertEquals("www.runoob.com", url.getHost());
		assertEquals("/index.html?language=cn", url.getFile());
		assertEquals("/index.html", url.getPath());
		assertEquals("j2me", url.getRef());
		assertEquals("language=cn", url.getQuery());
	}
	
	/**
	 * 加载资源一般用到Class.getResource和ClassLoader.getResource()
	 * Class.getResource:
	 * 	不以"/"开头时，默认从此类所在的包下获取资源
	 *  以"/"开头时，从ClassPath根下获取资源
	 * Class.getClassLoader().getResource(String path):
	 * 	path不能以"/"开头
	 *  path是从ClassPath根下获取资源
	 */
	@Test
	public void testClassLoaderResource(){
		//file:/Users/jerry/myself/github/jerry_test/target/classes/com/sun/java8/classloader/
		System.out.println(ClassLoaderTest.class.getResource(""));
		//file:/Users/jerry/myself/github/jerry_test/target/classes/
		System.out.println(ClassLoaderTest.class.getResource("/"));
		
		ClassLoaderTest test = new ClassLoaderTest();
		//class com.sun.java8.classloader.ClassLoaderTest
		System.out.println(test.getClass());
		//sun.misc.Launcher$AppClassLoader@2a139a55
		System.out.println(test.getClass().getClassLoader());
		//file:/Users/jerry/myself/github/jerry_test/target/classes/
		System.out.println(test.getClass().getClassLoader().getResource(""));
		//null
		System.out.println(test.getClass().getClassLoader().getResource("/"));
		
		URL url = test.getClass().getClassLoader().getResource("com/sun/java8/classloader/1.properties");
		//file:/Users/jerry/myself/github/jerry_test/target/classes/com/sun/java8/classloader/1.properties
		System.out.println("url : " + url);
		//file
		System.out.println("protocol : " + url.getProtocol());
		///Users/jerry/myself/github/jerry_test/target/classes/com/sun/java8/classloader/1.properties
		System.out.println("path : " + url.getPath());
		
	}
	
	public static void main(String[] args){
		ClassLoaderTest test = new ClassLoaderTest();
		test.testClassLoaderResource();
	}
	
}
