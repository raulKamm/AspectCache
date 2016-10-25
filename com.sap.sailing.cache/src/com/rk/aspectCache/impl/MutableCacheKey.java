package com.sap.sailing.cache.impl;

import java.util.Arrays;

import com.sap.sailing.cache.common.AbstractCacheKey;
import com.sap.sailing.cache.common.CachedMethodSignature;

/**
 * This implementation is mutable, in the sense that the field inputParameters references object that can be modified during the lifetime of this key.
 * Therefore, it should only be used when the arguments of the @Cached method are immutable.  Behavior when the arguments are modified is undefined.
 * 
 * @author Raul Bertone (D059912)
 */

public class MutableCacheKey extends AbstractCacheKey {

	private final Object[] inputParameters;
	private final int hashCode;
	
	public MutableCacheKey(CachedMethodSignature signature, Object[] inputParameters){
		super(signature);
		this.inputParameters = inputParameters;
		hashCode = 31 * super.hashCode() + Arrays.deepHashCode(inputParameters); // caching of the hashCode
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutableCacheKey other = (MutableCacheKey) obj;
		if (!Arrays.deepEquals(inputParameters, other.inputParameters))
			return false;
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
}
