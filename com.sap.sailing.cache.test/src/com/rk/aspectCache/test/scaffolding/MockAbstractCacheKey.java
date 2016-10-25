package com.rk.aspectCache.test.scaffolding;

import com.rk.aspectCache.common.AbstractCacheKey;
import com.rk.aspectCache.common.CachedMethodSignature;

public class MockAbstractCacheKey extends AbstractCacheKey {

	public MockAbstractCacheKey(CachedMethodSignature methodSignature) {
		super(methodSignature);
	}

	@Override
	public boolean isMutable() {
		return false;
	}

}
