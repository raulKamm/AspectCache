package com.sap.sailing.cache.test.tool;

public interface DataCollection{

	public Data getValue(int index);
	
	public void modify(int index); 
	
	public int getSize();
	public void setSize(int i);
	
}
