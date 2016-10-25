package com.sap.sailing.cache.impl;

import com.sap.sailing.cache.common.AbstractCacheKey;
import com.sap.sailing.cache.common.CachedMethodSignature;

/**
 * This implementation is immutable and uses the serialization of the @Cached method's arguments to
 * obtain a unique identity for the method invocation.
 * 
 * @author Raul Bertone (D059912)
 */

public class ImmutableCacheKey extends AbstractCacheKey {

	private final int hashCode;
	private final String closure; // string version of the serialization
	
	public ImmutableCacheKey(CachedMethodSignature signature, String closure) {
		super(signature);
		this.closure = closure;
		hashCode = 31 * super.hashCode() + closure.hashCode();  // caching of the hashCode
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
		ImmutableCacheKey other = (ImmutableCacheKey) obj;
		if (closure == null) {
			if (other.closure != null)
				return false;
		} else if (!closure.equals(other.closure))
			return false;
		return true;
	}

	public boolean isMutable(){
		return false;
	}

}
