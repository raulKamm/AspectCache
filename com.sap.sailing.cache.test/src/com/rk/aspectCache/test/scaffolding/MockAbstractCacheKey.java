package com.sap.sailing.cache.test.scaffolding;

import com.sap.sailing.cache.common.AbstractCacheKey;
import com.sap.sailing.cache.common.CachedMethodSignature;

public class MockAbstractCacheKey extends AbstractCacheKey {

	public MockAbstractCacheKey(CachedMethodSignature methodSignature) {
		super(methodSignature);
	}

	@Override
	public boolean isMutable() {
		return false;
	}

}
