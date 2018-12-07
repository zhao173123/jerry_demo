package com.sun.json.vo;

import java.io.Serializable;

public class Person implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Integer age;
	
	public Person(String name, Integer age){
		this.name = name;
		this.age = age;
	}
	
	public Person(){
		this.name = "Jerry";
		this.age = 30;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String toString(){
		return "name:" + name + ",age:" + age;
	}
}
