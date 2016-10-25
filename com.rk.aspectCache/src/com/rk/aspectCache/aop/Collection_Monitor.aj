package com.rk.aspectCache.aop;

import java.lang.ref.WeakReference;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.Cached;
import com.rk.aspectCache.common.MonitoredCollection;
import com.rk.aspectCache.common.MonitoredTask;
import com.rk.aspectCache.common.MonitoringParameters;
import com.rk.aspectCache.impl.CacheImpl;

/**
 * Root of the Java Collection Framework monitoring aspects hierarchy. <br>
 * It is instantiated pertarget, so that one instance will exist for each MonitoredCollection instance.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */
@SuppressAjWarnings
public abstract aspect Collection_Monitor pertarget(call(* *.*(..)) /*&& target(MonitoredCollection)*/){

	private boolean register = true;
	
	pointcut cachedMethod(): execution(@Cached * *.*(..)) || within(MonitoredTask+);
	pointcut excludeFramework(): !within(com.rk.aspectCache.impl.*) && !within(com.rk.aspectCache.aop.*) && !within(com.rk.aspectCache.common.*);
	
	/**
	 * Prevents calls from Iterators that happen outside the control-flow of a @Cached method to register a dependency when a List-element is accessed.
	 */
	pointcut cflowCheck(): call(* Collection_Monitor.iteratorNext(..)) && !cflow(cachedMethod());
	void around(): cflowCheck() {
		// do nothing if the call happens outside the control flow of a @Cached method
	}
	
	/**
	 * Called by IteratorWrappers to signal that the element at index has been removed.
	 * 
	 * @param index of the element that has been removed
	 */
	public abstract void iteratorRemove(int index);
	
	/**
	 * Called by IteratorWrappers to signal that the element at index has been accessed.
	 * 
	 * @param index of the element that has been accessed
	 */
	public abstract void iteratorNext(int index);
	
	protected void registerCollectionDependency(CacheKey key) {
		// register this Monitor to allow counting dependencies
		if (register) {
			register = false; // register it only once
			MonitoringParameters.registerCollection(new WeakReference<Collection_Monitor>(this));
		}
		
		CacheImpl.INSTANCE.registerCollectionDependency(this, key);
	}
	
	/**
	 * When a CacheKey is invalidated someplace else, dependencies registered in this monitor are removed by calling this method.
	 * 
	 * @param dependency the CacheKey that has been invalidated
	 */
	public abstract void removeDependency(CacheKey dependency);
	
	/**
	 * @return the numeber of dependencies currently registered for the wrapped Collection
	 */
	public abstract long countDependencies();
	
}
