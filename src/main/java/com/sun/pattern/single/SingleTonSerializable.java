package com.sun.pattern.single;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * 序列化和反序列化单列模式
 * @author jerry
 *
 */
public class SingleTonSerializable implements Serializable{

	private static final long serialVersionUID = 1l;
	
	private SingleTonSerializable(){
		
	}
	
	//内部类
	private static class SingleTonSerializableHandler{
		private static SingleTonSerializable instance = new SingleTonSerializable();
	}
	
	public static SingleTonSerializable getInstance(){
		return SingleTonSerializableHandler.instance;
	}
	
	//该方法在反序列化的时候会被调用，该方法不是接口定义的方法（约定俗成）
	protected Object readResolve() throws ObjectStreamException{
		System.out.println("调用了readResolve接口！");
		return SingleTonSerializableHandler.instance;
	}
	
}
