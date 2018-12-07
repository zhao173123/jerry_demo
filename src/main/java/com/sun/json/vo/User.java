package com.sun.json.vo;

import com.google.gson.annotations.SerializedName;

public class User {

	private String name;
	private int age;
	//支持前端3中输入方式；当多种情况出现时，以最后一个出现的值为准
	@SerializedName(value = "emailAddress")
	private String emailAddress;

	public User(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public User() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String toString() {
		String result = "name:" + this.name + ",age:" + this.age + ",email:" + this.emailAddress;
		return result;
	}

}
