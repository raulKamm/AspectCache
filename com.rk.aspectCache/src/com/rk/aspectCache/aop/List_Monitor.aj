package com.rk.aspectCache.aop;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.impl.CacheImpl;
import com.rk.aspectCache.impl.IteratorWrapper;
import com.rk.aspectCache.impl.ListIteratorWrapper;

/**
 * Monitors calls to objects implementing the List interface.
 *  
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

@SuppressWarnings("rawtypes")
@SuppressAjWarnings
public aspect List_Monitor extends Collection_Monitor {
	
	private final ConcurrentSkipListMap<Integer, HashSet<CacheKey>> listElementToCacheKeys = new ConcurrentSkipListMap<Integer, HashSet<CacheKey>>(); // List-element - CacheKey dependencies
	private final ConcurrentHashMap<CacheKey, HashSet<Integer>> cacheKeyToListElements = new ConcurrentHashMap<CacheKey, HashSet<Integer>>(); // CacheKey - List-element dependencies
	
	/**
	 * Invalidates every CacheKey associated with previous get(int) calls to this List where index >= (index of the added element) and removes them from this List dependencies
	 * 
	 * @param index at which the new element will be added
	 */
	pointcut listAddAt(int index): call(* List.add(int, *)) && args(index, *) && excludeFramework();
	after(int index) returning(): listAddAt(index) {
		invalidateTail(index);
	}
	
	/**
	 * Invalidates every CacheKey associated with previous get(int) calls to this List where index >= (position of the removed element). Removes the affected CacheKeys
	 * from this List dependencies
	 * 
	 * @param element to be removed
	 */
	pointcut listRemoveElement(Object element, List lst): call(boolean List.remove(*)) && args(element) && target(lst) && excludeFramework();
	after(Object element, List lst) returning(): listRemoveElement(element, lst) {
		int index = lst.indexOf(element);
		if (index != -1) { // indexOf returns -1 if the element is not present
			invalidateTail(index);
		}
	}
	
	/**
	 * Invalidates every CacheKey associated with previous get(int) calls to this List where index >= (index of the removed element). Removes the affected CacheKeys
	 * from this List dependencies
	 * 
	 * @index of the element to be removed
	 */
	pointcut listRemoveAt(int index): call(* List.remove(int)) && args(index) && excludeFramework();
	after(int index) returning(): listRemoveAt(index) {
		invalidateTail(index);
	}

	/**
	 * Registers a dependency of the current calculating CacheKey to the requested List-element 
	 * 
	 * @param index
	 */
	pointcut listGet(int index): call(* List.get(int)) && args(index) && cflow(cachedMethod()) && excludeFramework();
	after(int index) returning(): listGet(index) {
		get(index);
	}
	
	/**
	 * Returns the requested Iterator wrapped in an IteratorWrapper
	 */
	pointcut iterator(): call(Iterator Collection.iterator()) && target(java.util.List) && excludeFramework();
	@SuppressWarnings("unchecked")
	Iterator around(): iterator() {
		return new IteratorWrapper(proceed(), this);
	}
	
	/**
	 * Returns the requested ListIterator wrapped in an ListIteratorWrapper
	 */
	pointcut listIterator(): call(ListIterator List.listIterator()) && excludeFramework();
	@SuppressWarnings("unchecked")
	ListIterator around(): listIterator() {
		return new ListIteratorWrapper(proceed(), this);
	}
	
	/**
	 * Returns the requested ListIterator wrapped in an ListIteratorWrapper set to the provided index.
	 */
	pointcut listIteratorAt(int index): call(ListIterator List.listIterator(int)) && args(index) && excludeFramework();
	@SuppressWarnings("unchecked")
	ListIterator around(int index): listIteratorAt(index) {
		return new ListIteratorWrapper(proceed(index), this, index);
	}
	
	/**
	 * Invalidates every CacheKey associated with previous get(int) calls to this List where index == (index of the removed element). Removes the affected CacheKeys
	 * from this List dependencies
	 * 
	 * @index of the element to be set
	 */
	pointcut listSet(int index, Object obj): call(* List.set(int, Object)) && args(index, obj) && excludeFramework();
	after(int index, Object obj) returning(): listSet(index, obj) {
		invalidateIndex(index);
	}
	
	
	/**
	 * Invalidates the CacheKeys associated with index and removes the dependencies.
	 * 
	 * @param index
	 */
	private void invalidateIndex(int index) {
		long invalidationTime = System.nanoTime();
		HashSet<CacheKey> CacheKeySet = new HashSet<CacheKey>();
		boolean isThereAnythingToInvalidate = false;
		
		synchronized (listElementToCacheKeys) {
			isThereAnythingToInvalidate = listElementToCacheKeys.containsKey(index);
			if (isThereAnythingToInvalidate) {
				CacheKeySet = listElementToCacheKeys.get(index); 
				listElementToCacheKeys.remove(index);
			}
		}
		
		if(isThereAnythingToInvalidate) {
			CacheImpl.INSTANCE.invalidate(CacheKeySet, invalidationTime);
		}
	}
	
	/**
	 * Invalidates the CacheKeys associated with indexes >= (@param index) and removes the dependencies.
	 * 
	 * @param index
	 */
	private void invalidateTail(int index) {
		long invalidationTime = System.nanoTime();
		HashSet<CacheKey> keys = new HashSet<CacheKey>();
		
		synchronized (listElementToCacheKeys) {
			ConcurrentNavigableMap<Integer, HashSet<CacheKey>> tailMap = listElementToCacheKeys.tailMap(index);
			
			for (Integer key: tailMap.navigableKeySet()) {
				keys.addAll(tailMap.get(key));
				tailMap.remove(key);
			}
		}
	
		CacheImpl.INSTANCE.invalidate(keys, invalidationTime);
	}
	
	/**
	 * Called by an Iterator when an add() operation is performed through it
	 * 
	 * @param index of the added element
	 */
	public void iteratorAdd(int index) {
		invalidateTail(index);
	}
	
	/**
	 * Called by an Iterator when an add() operation is performed through it
	 * 
	 * @param index of the added element
	 */
	public void iteratorSet(int index) {
		invalidateIndex(index);
	}
	
	/**
	 * Called by an Iterator when a next()/previous() operation is performed on it
	 * 
	 * @param index of the added element
	 */
	public void iteratorNext(int index) {
		get(index);
	}
	
	@Override
	public void iteratorRemove(int index) {
		invalidateTail(index);
	}
	
	/**
	 * Creates a dependency of the currently calculating CacheKey to the List-element at index.
	 * Registers the dependency into the CacheValueContainer associated with the CacheKey.
	 * 
	 * @param index
	 */
	public void get(int index) {
		CacheKey key = DependencyThreadLocal.INSTANCE.getDependency(); // TODO synchronize on both Maps, not just one at a time or even better synchronize on the CacheKey
		
		synchronized (listElementToCacheKeys) {
			listElementToCacheKeys.putIfAbsent(index, new HashSet<CacheKey>());
			listElementToCacheKeys.get(index).add(key);
		}
		
		synchronized (cacheKeyToListElements) {
			cacheKeyToListElements.putIfAbsent(key, new HashSet<Integer>());
			cacheKeyToListElements.get(key).add(index);
		}
		
		registerCollectionDependency(key);
	}
	
	@Override
	public void removeDependency(CacheKey key) { // TODO synchronize on both Maps, not just one at a time or even better synchronize on the CacheKey
		HashSet<Integer> indexes;
		
		synchronized (cacheKeyToListElements) {
			indexes = cacheKeyToListElements.get(key);
			cacheKeyToListElements.remove(key);
		}
				
		synchronized (listElementToCacheKeys) {
			
			for (Integer i: indexes) {
				if (listElementToCacheKeys.containsKey(i)) { // it might have been already removed if the invalidation started from this same List_Monitor, for example in the method invalidateIndex()
					listElementToCacheKeys.get(i).remove(key);
				}
			}
		}
	}
	
	@Override
	public long countDependencies() {
		long count = 0;
		for (HashSet<CacheKey> set: listElementToCacheKeys.values()){
			count += set.size();
		}
		return count;
	}
	
}
