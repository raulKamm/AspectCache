package com.rk.aspectCache.common;

import java.util.Vector;

import com.rk.aspectCache.common.CacheKey;

/**
 * Provides static access to the current dependencies, that is, the cache elements - represented by their associated CacheKeys - being calculated by the current thread.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

public class DependencyThreadLocal {

	// Singleton pattern
	public static final DependencyThreadLocal INSTANCE = new DependencyThreadLocal();
	
	private final ThreadLocal<Vector<CacheKey>> dependencies = new ThreadLocal<Vector<CacheKey>>() {
																						@Override protected Vector<CacheKey> initialValue() {
																							return null;
																						}
																					 };

	private DependencyThreadLocal() {
		// this 0 arguments constructor exists only to prevent instantiation
	}
																					 
	/**
	 * Adds a CacheKey to the ThreadLocal
	 * 
	 * @param key to be added
	 */
	public void setCacheElementCalculationID(CacheKey key) {
		Vector<CacheKey> v = dependencies.get();

		// if threadLocal is empty create a new Vector in it
		if(v==null){
			v = new Vector<CacheKey>(1,1);
			dependencies.set(v);
		}
		// threadLocal is not empty, add an element to the Vector
		v.add(key);
	}

	/**
	 * Removes the {@link CacheKey} that identifies the calculation
	 */
	public void removeCacheElementCalculationID() {
		dependencies.get().remove(dependencies.get().size() - 1);
	}
	
	/**
	 * @return the CacheKey undergoing calculation in the current thread
	 */
	public CacheKey getDependency(){
		// this check is needed only for the TaskWrapper and Recalculator cases
		if (dependencies.get().isEmpty()){
			return null;
		} else {
			return dependencies.get().lastElement();
		}
		
	}
	
	/**
	 * Differs from getDependency() only for the null check: in this case the Vector might not exist and finding a null is a possibility.
	 * 
	 * @return if it exists, the parent cache key of the one currently under calculation
	 */
	public CacheKey getNestedDependency(){
		if (dependencies.get() == null || dependencies.get().isEmpty() ) {
			return null;
		} else {
			return dependencies.get().lastElement();
		}
	}
} 