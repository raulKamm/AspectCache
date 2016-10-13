package com.sap.sailing.cache.test.scaffolding;

import com.sap.sailing.cache.aop.Collection_Monitor;
import com.sap.sailing.cache.common.AbstractCacheValueContainer;
import com.sap.sailing.cache.common.CacheKey;
import com.sap.sailing.cache.common.FieldID;
import com.sap.sailing.cache.impl.CacheFetch;

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
