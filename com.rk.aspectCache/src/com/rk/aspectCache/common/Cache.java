package com.rk.aspectCache.common;

import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.util.HashSet;

import com.rk.aspectCache.aop.Collection_Monitor;
import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.impl.CacheFetch;

/**
 * The centerpiece of the caching framework. Manages the actual cache data structure. Called by Cached_Methods to request/provide a cache value - identified
 * by a {@link CacheKey}. Monitored fields register here their dependencies, again providing a {@link CacheKey} and when they are eventually modified they
 * invoke the invalidation of the dependent cache elements and the removal of the previously registered dependencies.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

public interface Cache {
	
	/**
	 * Tries to return the current value associated with the provided CacheKey. <br>
	 * If the value is not stored in the cache it creates an empty CacheValueContainer and returns its state.
	 * 
	 * @param key The key associated with the value
	 * @param waitForFresh in case a stale value is present and a new one is already being calculated, whether to wait for grab the former or wait for the latter
	 * @param stable Is the invoking @Cached method declared stable?
	 * @return A CacheFetch object representing the current state of the requested value
	 */
	CacheFetch fetch(CacheKey key, boolean waitForFresh, boolean stable);
	
	/**
	 * Tries to return the current value associated with the provided CacheKey. <br>
	 * If the value is not stored in the cache it creates an empty CacheValueContainer and returns its state.
	 * 
	 * @param key The key associated with the value
	 * @param waitForFresh in case a stale value is present and a new one is already being calculated, whether to wait for grab the former or wait for the latter
	 * @param instance The object on which the @Cached method has been invoked upon. Null if it's a static method
	 * @param method The @Cached method
	 * @param args The arguments the @Cached method has been invoked with
	 * @return A CacheFetch object representing the current state of the requested value
	 */
	CacheFetch fetch(CacheKey key, boolean waitForFresh, Object instance, Method method, Object[] args);
	
	/**
	 * Substitutes the current value associated with key with the provided one
	 * 
	 * @param key
	 * @param value
	 */
	void update(CacheKey key, Object value);
	
	/**
	 * Registers the dependency of a CacheKey from a monitored field
	 * 
	 * @param fieldID identifying the field that has been read
	 * @param CacheKey the cache element being calculated by the current thread
	 */
	void registerFieldDependency(FieldID fieldID, CacheKey newDependency);
	
	/**
	 * Registers the dependency of a CacheKey from a monitored Collection or Map
	 * 
	 * @param collection that has been read
	 * @param CacheKey the cache element being calculated by the current thread
	 */
	void registerCollectionDependency(Collection_Monitor collection, CacheKey newDependency);
	
	/**
	 * Invalidates all CacheKeys that depend on the field.
	 * 
	 * @param fieldID identifying the field which has been modified
	 * @param InvalidationTimeStamp the time at which the field was modified
	 */
	void invalidateDataField(FieldID field, long InvalidationtimeStamp);
	
	/**
	 * Invalidates the values associated with the provided CacheKeys
	 * 
	 * @param cacheKeySet the CacheKeys to be invalidated
	 * @param InvalidationTimeStamp the time at which the invalidation occurred
	 */
	void invalidate(HashSet<CacheKey> cacheKeySet, long InvalidationTimeStamp);

	/**
	 * @return The current status of the value associated with key
	 */
	CacheValueStatus getValueStatus(CacheKey key);
	
	ReferenceQueue<? super Object> getReferenceQueue();

	/**
	 * The following methods are used for calculating and retrieving statistics. <br>
	 * Autocache provides a MBean to retrieve them.
	 */
	long getCacheHits();

	long getCacheMisses();

	long getStaleObjectsNumber();

	int getCacheEntriesNumber();

	long getDependencies();
	
	long getTotalMissTime();
	
	long getTotalHitTime();
	
	long getTotalInvalidations();
	
	long getTotalInvalidationTime();

	void addCacheHit(long timeBefore);

	void addCacheMiss(long timeBefore);

	int getFieldNumber();

	void resetCounters(long i);

}
