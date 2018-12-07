package com.sun.pattern.single;

public class ClassFactory {

	private enum MyEnumSingleton{
		
		singletonFactory2;
		
		private MySingleTon2 instance;
		
		private MyEnumSingleton(){//枚举类的构造方法在类加载时被实例化
			instance = new MySingleTon2();
		}
 
		public MySingleTon2 getInstance2(){
			return instance;
		}
	} 
 
	public static MySingleTon2 getInstance2(){
		MySingleTon2 mySingleTon2 = MyEnumSingleton.singletonFactory2.getInstance2();
		return mySingleTon2;
	}
}

//需要获实现单例的类
class MySingleTon2 {
	public MySingleTon2(){}
}