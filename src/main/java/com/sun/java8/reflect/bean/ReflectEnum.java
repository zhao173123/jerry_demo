package com.sun.java8.reflect.bean;

public enum ReflectEnum {

	COMMON(0), STANDAED(1), SPECIAL(2);

	private int _state;

	ReflectEnum(int state) {
		_state = state;
	}

	public int val() {
		return this._state;
	}
}
