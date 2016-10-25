package com.rk.aspectCache.common;

public abstract class AbstractFieldID implements FieldID {

	// only to force overriding in subclasses
	abstract public int hashCode();
		
	// only to force overriding in subclasses
	abstract public boolean equals(Object obj);
	
}
