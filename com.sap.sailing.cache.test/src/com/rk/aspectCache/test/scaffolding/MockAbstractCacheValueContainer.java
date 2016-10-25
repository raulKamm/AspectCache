package com.rk.aspectCache.test.scaffolding;

import com.rk.aspectCache.aop.Collection_Monitor;
import com.rk.aspectCache.common.AbstractCacheValueContainer;
import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.FieldID;
import com.rk.aspectCache.impl.CacheFetch;

public class MockAbstractCacheValueContainer extends AbstractCacheValueContainer {

	@Override
	public CacheFetch getOrCalculateIfStale(boolean waitForFresh) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate(CacheKey key, long timeStamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Object value) {
		// TODO Auto-generated method stub

	}

	public boolean checkFieldDependency(FieldID field){
		return fieldDependencies.contains(field);
	}
	
	public boolean checkNestedDependency(CacheKey key){
		return nestedDependencies.contains(key);
	}
	
	public boolean checkCollectionDependency(Collection_Monitor monitor){
		return collectionDependencies.contains(monitor);
	}
}
