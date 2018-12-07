package com.sun.java8.reflect.type;

import java.lang.reflect.Type;

public interface MyParameterizedType extends Type {

	//返回Map<String, T>里的String和T，所以这里返回[String.class,T.class]
	Type[] getActualTypeArguments();
	
	//Map<String,T>里的Map,所以返回值是Map.class
	Type getRawType();
	
	//用于这个泛型上中包含了内部类的情况,一般返回null
	Type getOwnerType();
	
}
