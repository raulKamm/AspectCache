package com.sap.sailing.cache.common;

import java.util.HashSet;

import com.sap.sailing.cache.aop.Collection_Monitor;

public abstract class AbstractCacheValueContainer implements CacheValueContainer {

	protected Object value; // the cached value wrapped by this container
	protected long timeStamp; // the time at which the calculation for the current value started
	protected final HashSet<CacheKey> nestedDependencies; // cached entries that used this value in their calculation ("nested calls")
	protected final HashSet<FieldID> fieldDependencies; // monitored fields that were read while calculating the current value
	protected final HashSet<Collection_Monitor> collectionDependencies; // monitored collections that where read while calculating the current value
	protected volatile CacheValueStatus status; // the freshness status of the current value
	
	protected AbstractCacheValueContainer(){
		status = CacheValueStatus.EMPTY_CALCULATING;
		fieldDependencies = new HashSet<FieldID>();
		collectionDependencies = new HashSet<Collection_Monitor>();
		nestedDependencies = new HashSet<CacheKey>();
		registerNestedDependency();
		timeStamp = System.nanoTime();
	}
	
	@Override
	public synchronized void registerNestedDependency() {
		CacheKey dependency = DependencyThreadLocal.INSTANCE.getNestedDependency();
		if (dependency != null){
			nestedDependencies.add(dependency);
		}
	}

	@Override
	public CacheValueStatus getStatus() {
		return status;
	}

	@Override
	public synchronized void registerFieldDependency(FieldID field) {
		fieldDependencies.add(field);
	}
	
	@Override
	public synchronized void registerCollectionDependency(Collection_Monitor collection) {
		collectionDependencies.add(collection);
	}

}
