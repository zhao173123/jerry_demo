package com.sun.java8.reflect.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterTypeImpl implements ParameterizedType {
	
	private final Class<?> raw;
	private final Type[] args;
	
	public ParameterTypeImpl(Class<?> raw, Type[] args){
		this.raw = raw;
		this.args = args;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return args;
	}

	@Override
	public Type getRawType() {
		return raw;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}

}
