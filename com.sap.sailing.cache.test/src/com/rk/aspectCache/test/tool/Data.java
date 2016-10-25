package com.sap.sailing.cache.test.tool;

public class Data {

	private Integer value = 0;
	
	public Data(int position) {
		this.value = position;
	}

	public synchronized Integer getPosition() {
		return value;
	}

	public synchronized void setPosition(int position) {
		this.value = position + this.value;
	}

}
