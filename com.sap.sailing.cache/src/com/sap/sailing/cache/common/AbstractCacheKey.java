package com.sap.sailing.cache.common;
 
public abstract class AbstractCacheKey implements CacheKey {

	// TODO now a new CachedMethodSignature object gets created for every CacheKey, even if the cached methods are just a handful. They should be saved in @Cached_Method and only a reference passed to the CacheKeys.
	protected final CachedMethodSignature methodSignature; // the @Cached method that generated this CacheKey
	
	public AbstractCacheKey (CachedMethodSignature methodSignature) {
		this.methodSignature = methodSignature;
	}
	
	@Override
	abstract public boolean isMutable();

	@Override
	public int hashCode() {
		return methodSignature.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCacheKey other = (AbstractCacheKey) obj;
		if (methodSignature == null) {
			if (other.methodSignature != null)
				return false;
		} else if (!methodSignature.equals(other.methodSignature))
			return false;
		return true;
	}

}
