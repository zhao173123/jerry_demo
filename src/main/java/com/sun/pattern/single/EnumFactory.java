package com.sun.pattern.single;

public enum EnumFactory {

	singleTonFactory;

	private MySingleTon instance;

	private EnumFactory() {
		instance = new MySingleTon();
	}

	public MySingleTon getInstance() {
		return instance;
	}
}

class MySingleTon {
	public MySingleTon() {
	}
}
