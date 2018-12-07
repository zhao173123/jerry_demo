package com.sun.json.vo;

import java.util.Date;

public class UserDate {

	private String _name;
	private String email;
	private boolean isDeveloper;
	private int age;
	private Date register = new Date();

	public UserDate(String _name, String email, int age, boolean isDeveloper) {
		this._name = _name;
		this.email = email;
		this.age = age;
		this.isDeveloper = isDeveloper;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isDeveloper() {
		return isDeveloper;
	}

	public void setDeveloper(boolean isDeveloper) {
		this.isDeveloper = isDeveloper;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getRegister() {
		return register;
	}

	public void setRegister(Date register) {
		this.register = register;
	}

}
