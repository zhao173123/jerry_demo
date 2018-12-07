package com.sun.json.vo;

public class GenericModel<T> {

	T value;
	
	public GenericModel(T value){
		super();
		this.value = value;
	}
	
	@Override
	public String toString(){
		return "model[value=" + value + "]";
	}
	
}
