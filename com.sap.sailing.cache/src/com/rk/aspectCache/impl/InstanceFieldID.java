package com.rk.aspectCache.impl;

import com.rk.aspectCache.common.AbstractFieldID;

public class InstanceFieldID extends AbstractFieldID{

	private final int declaringObject; // output of System.identityHashCode()
	private final String fieldName; 
	private final int hashCode; // since this object is immutable, the hashCode is cached
	
	public InstanceFieldID (int declaringObject, String fieldName) {
		 this.declaringObject = declaringObject;
		 this.fieldName = fieldName;
		 hashCode = declaringObject + 31 * fieldName.hashCode();
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
		InstanceFieldID other = (InstanceFieldID) obj;
		if (declaringObject != other.declaringObject)
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}

}
