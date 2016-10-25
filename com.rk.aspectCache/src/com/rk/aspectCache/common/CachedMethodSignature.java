package com.rk.aspectCache.common;

import java.util.Arrays;

import org.aspectj.lang.reflect.MethodSignature;

/**
 * Identifies a @Cached method's signature. It is used in place of the MethodSignature type from the AspectJ Runtime API to have complete control on <br>
 * the implementation of hashCode() and equals(), which is necessary since this is used in building a CacheKey.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

/*
 * TODO after Cached_Method will be modified to create a single instance of CachedMethodSignature per cached method, this could be simplified removing every field but the hashCode and
 * leaving only the identity check in equals()
 */
public class CachedMethodSignature {

	private final String[] parametersType; 
	private final String returnType;
	private final String declaringTypeName;
	private final String name;
	private final int hashCode;
	
	public CachedMethodSignature (MethodSignature signature) {
		Class<?>[] parameters = signature.getParameterTypes();
		parametersType = new String[parameters.length];
		
		for (int i = 0; i< parameters.length; i++) {
			parametersType[i] = parameters[i].getName();
		}
		
		name = signature.getName();
		returnType = signature.getReturnType().getName();
		declaringTypeName = signature.getDeclaringType().getName();
		hashCode = cacheHashCode();
	}
	
	/*
	 * the type is immutable so the hashCode can be cached
	 */
	private int cacheHashCode(){
		int hashValue = 0;
		int prime = 31;
		
		for (String str : parametersType){
			hashValue = prime * hashValue + str.hashCode();
		}
		
		hashValue = prime * hashValue + name.hashCode();
		hashValue = prime * hashValue + returnType.hashCode();
		hashValue = prime * hashValue + declaringTypeName.hashCode();
		
		return hashValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CachedMethodSignature other = (CachedMethodSignature) obj;
		if (declaringTypeName == null) {
			if (other.declaringTypeName != null)
				return false;
		} else if (!declaringTypeName.equals(other.declaringTypeName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(parametersType, other.parametersType))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
}
