package com.rk.aspectCache.common;

import com.rk.aspectCache.aop.Collection_Monitor;
import com.rk.aspectCache.impl.CacheFetch;

/**
 * Wrapper for cache elements: provides the additional behavior, namely the freshness status
 * of the value. Also it is in charge of handling concurrent access to the wrapped object.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 *
 */

public interface CacheValueContainer{

	/**
	 * Registers the parent cache value currently being calculated (if one exists), of which the value stored in this container constitutes a dependency
	 */
	void registerNestedDependency();

	/**
	 * @return The current freshness status of the stored value
	 */
	CacheValueStatus getStatus();

	/**
	 * Returns the current stored value if fresh.
	 * The contract of this method requires the caller to calculate a new value if the current one is stale and a new value is not already being calculated
	 * 
	 * @param waitForFresh In case a new value is already being calculated, whether to wait for it or accept the current stale one
	 * @return
	 */
	CacheFetch getOrCalculateIfStale(boolean waitForFresh);

	/**
	 * Registers that {@param field} has been read while while calculating the current value stored in this container
	 * 
	 * @param field A monitored field
	 */
	void registerFieldDependency(FieldID field);
	
	/**
	 * Registers that {@param collection} has been read while while calculating the current value stored in this container
	 * 
	 * @param collection A monitored Collection (or Map)
	 */
	void registerCollectionDependency(Collection_Monitor collection);

	/**
	 * Invalidates the current stored value, that is, sets the freshness value to "stale"
	 * 
	 * @param key The CacheKey associated with this container
	 * @param timeStamp The time at which the invalidation was fired (i.e the monitored field was modified)
	 */
	void invalidate(CacheKey key, long timeStamp);
	
	/**
	 * Updates the value stored by the container
	 * 
	 * @param value The new value to be stored in this container
	 */
	void update(Object value);

}
