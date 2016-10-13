package com.sap.sailing.cache.test.tool;

import java.util.LinkedList;

import com.sap.sailing.cache.common.MonitoredCollection;

public class DataList extends LinkedList<Data> implements DataCollection, MonitoredCollection{

	private static final long serialVersionUID = 1L;
	
	public DataList(int i) {
		for (int j=0; j<i; j++) {
			add(new Data((int)Math.rint(Math.random() * 100)));
		}
	}
	
	@Override
	public synchronized Data getValue(int index){
		return get(index);
	}
	
	@Override 
	public synchronized void modify(int index){
		set(index, get(index)); // the object in the collection is substituted with itself: it's enough to fool Autocache...
	}

	@Override
	public int getSize() {
		return size();
	}

	@Override
	public void setSize(int newSize) {
		int difference = size() - newSize;
				
		// remove excess elements
		while (difference > 0) {
			remove();
			difference--;
		}
		
		// increase size to newSize
		while (difference < 0) {
			add(new Data((int)Math.rint(Math.random() * 100)));
			difference++;
		}
	}
}
