package com.sap.sailing.cache.impl;

import com.sap.sailing.cache.common.AbstractFieldID;

public class StaticFieldID extends AbstractFieldID{

	private final String declaringClass; // fully qualified name of the declaring class
	private final String fieldName;
	private final int hashCode; // since this object is immutable, the hashCode is cached
	
	public StaticFieldID (String declaringClass, String fieldName) {
		 this.declaringClass = declaringClass;
		 this.fieldName = fieldName;
		 hashCode = declaringClass.hashCode() + 31 * fieldName.hashCode();
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaticFieldID other = (StaticFieldID) obj;
		if (declaringClass == null) {
			if (other.declaringClass != null)
				return false;
		} else if (!declaringClass.equals(other.declaringClass))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}
	
}
