package com.rk.aspectCache.impl;

import java.lang.management.ManagementFactory;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.rk.aspectCache.aop.Collection_Monitor;
import com.rk.aspectCache.common.AbstractCacheValueContainer;
import com.rk.aspectCache.common.Cache;
import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.CacheValueContainer;
import com.rk.aspectCache.common.CacheValueStatus;
import com.rk.aspectCache.common.FieldID;

/**
 * This implementation of the {@link Cache} interface uses the Singleton pattern to provide access to (the unique instance of) the cache data structure. <br>
 * It is based on {@link ConcurrentHashMap}s and therefore hands off to them most of the concurrency management.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

public class CacheImpl implements Cache{
	
	// Singleton pattern
	public final static Cache INSTANCE = new CacheImpl();
	
	private final ConcurrentHashMap<CacheKey, CacheValueContainer> cache; // all the framework works for this: the actual cached values!
	private final ConcurrentHashMap<FieldID, HashSet<CacheKey>> dependencyMapping; // stores the dependencies between cache values and the fields that were read during their calculation
	private final ReferenceQueue<? super Object> deadReferences; // dead references to arguments in IdentityCacheKeys
	private AtomicLong cacheHits = new AtomicLong(0);
	private AtomicLong cacheMisses = new AtomicLong(0);
	private AtomicLong staleObjects = new AtomicLong(0);
	private AtomicLong totalHitTime = new AtomicLong(0);
	private AtomicLong totalMissTime = new AtomicLong(0);
	private AtomicLong invalidations = new AtomicLong(0);
	private AtomicLong totalInvalidationTime = new AtomicLong(0);
	
	// This private 0 arguments constructor is part of the Singleton pattern: don't remove
	private CacheImpl(){
		// TODO read from config file the initialization parameters
		cache = new ConcurrentHashMap<CacheKey, CacheValueContainer>();
		dependencyMapping = new ConcurrentHashMap<FieldID, HashSet<CacheKey>>();
		deadReferences = new ReferenceQueue<Object>();
		try {
			registerMBean();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void registerMBean() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName name = new ObjectName("com.rk.aspectCache.impl:type=CacheMonitor");
        CacheMonitor mbean = new CacheMonitor(); 
		mbs.registerMBean(mbean, name);
	}
	
	@Override
	public long getCacheHits() {
		return cacheHits.get();
	}
	
	@Override
	public long getCacheMisses() {
		return cacheMisses.get();
	}
	
	@Override
	public long getTotalMissTime() {
		return totalMissTime.get();
	}
	
	@Override
	public long getTotalHitTime() {
		return totalHitTime.get();
	}
	
	@Override
	public long getTotalInvalidationTime() {
		return totalInvalidationTime.get();
	}

	@Override
	public long getTotalInvalidations() {
		return invalidations.get();
	}
	
	@Override
	public long getStaleObjectsNumber() {
		return staleObjects.get();
	}

	@Override
	public int getCacheEntriesNumber() {
		return cache.size();
	}
	
	@Override
	public long getDependencies() {
		long dependencies = 0;
		for (HashSet<CacheKey> set : dependencyMapping.values()) {
			dependencies += set.size();
		}
		return dependencies;
	}
	
	@Override
	public int getFieldNumber() {
		return dependencyMapping.size();
	}
	
	@Override
	public void resetCounters(long i) {
		if (i < 0) {
			cacheHits.set(0);
			cacheMisses.set(0);
			totalHitTime.set(0);
			totalMissTime.set(0);
			invalidations.set(0);
			totalInvalidationTime.set(0);
		}
	}
	
	@Override
	public void addCacheHit(long startTime){
		// if CPU time monitoring is deactivated/non-available in this JVM, fallback using System.nanotime
		long endTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			endTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			endTime = System.nanoTime();
		}
		resetCounters(cacheHits.incrementAndGet());
		resetCounters(totalHitTime.addAndGet(endTime - startTime));
	}
	
	@Override
	public void addCacheMiss(long startTime) {
		// if CPU time monitoring is deactivated/non-available in this JVM, fallback using System.nanotime
		long endTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			endTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			endTime = System.nanoTime();
		}
		resetCounters(cacheMisses.incrementAndGet());
		resetCounters(totalMissTime.addAndGet(endTime - startTime));
	}
	
	@Override
	public CacheValueStatus getValueStatus(CacheKey key){
		if(cache.containsKey(key)){
			return cache.get(key).getStatus();
		}
		return CacheValueStatus.EMPTY;
	}
	
	@Override
	public CacheFetch fetch(CacheKey key, boolean waitForFresh, boolean stable) {
		removeDeadKeys();
		if (cache.containsKey(key)){
			return cache.get(key).getOrCalculateIfStale(waitForFresh);
		} else {
			return newCacheKey(key, waitForFresh, stable);
		}
	}
	
	@Override
	public CacheFetch fetch(CacheKey key, boolean waitForFresh, Object instance, Method method, Object[] args) {
		removeDeadKeys();
		if (cache.containsKey(key)){
			return cache.get(key).getOrCalculateIfStale(waitForFresh);
		} else {
			return newCacheKey(key, waitForFresh, instance, method, args);
		}
	}
	
	/*
	 * Checks for the presence of dead WeakReferences and removes from the cache the keys (and their associated values) they belonged to.
	 */
	private void removeDeadKeys(){
		while (true) {
			ArgsWeakReference<?> ref = (ArgsWeakReference<?>)deadReferences.poll();
			if(ref != null) {
				cache.remove(ref.getOwner());
			} else {
				break;
			}
		}
	}
	
	/**
	 * This is synchronized (double check synchronization) to avoid the case in which two or more threads look-up at the same time for a cache element that doesn't exist yet (no object to synchronize
	 * upon) and end up creating more then one instance of it. 
	 * 
	 * @param key
	 * @return
	 */
	private synchronized CacheFetch newCacheKey(CacheKey key, boolean waitForFresh, Object instance, Method method, Object[] args) {
		if (cache.containsKey(key)){
			return cache.get(key).getOrCalculateIfStale(waitForFresh);
		} else {
			cache.put(key, new CacheValueContainerAutoRecalc(instance, method, args, key));
			return new CacheFetch(null, true);
		}
	}

	/**
	 * This is synchronized (double check synchronization) to avoid the case in which two or more threads look-up at the same time for a cache element that doesn't exist yet (no object to synchronize
	 * upon) and end up creating more then one instance of it. 
	 * 
	 * @param key
	 * @return
	 */
	public synchronized CacheFetch newCacheKey(CacheKey key, boolean waitForFresh, boolean stable){
		if (cache.containsKey(key)){
			return cache.get(key).getOrCalculateIfStale(waitForFresh);
		} else {
			cache.put(key, new CacheValueContainerImmutable(stable));
			return new CacheFetch(null, true);
		}
	}
	
	@Override
	public void update(CacheKey key, Object value) {
		if (cache.containsKey(key)) {
			cache.get(key).update(value);
		}
	}
	
	@Override
	public void registerFieldDependency(FieldID field, CacheKey newDependency) {
		if ( dependencyMapping.get(field) == null) {
			dependencyMapping.put(field, new HashSet<CacheKey>());			
		}
		dependencyMapping.get(field).add(newDependency);
		if (cache.containsKey(newDependency)) {
			cache.get(newDependency).registerFieldDependency(field);
		}
	}

	@Override
	public void registerCollectionDependency(Collection_Monitor collection, CacheKey newDependency) {
		if (cache.containsKey(newDependency)) {
			cache.get(newDependency).registerCollectionDependency(collection);
		}
	}
	
	@Override
	public void invalidate(HashSet<CacheKey> keys, long InvalidationTimeStamp) {
		long startTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			startTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			startTime = System.nanoTime();
		}
		
		for (CacheKey key : keys) {
			if (cache.containsKey(key)) {
				cache.get(key).invalidate(key, InvalidationTimeStamp);
			}
		}
		
		long endTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			endTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			endTime = System.nanoTime();
		}
		resetCounters(invalidations.incrementAndGet());
		resetCounters(totalInvalidationTime.addAndGet(endTime - startTime));
	}
	
	@Override
	// TODO tutto questo metodo ha la concorrenza sbagliata: non posso fare clear() se prima non blocco la collezione
	public void invalidateDataField(FieldID field, long fieldInvalidationTimeStamp) {
		long startTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			startTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			startTime = System.nanoTime();
		}
		
		if (dependencyMapping.get(field) != null) {
			HashSet<CacheKey> mappingCopy = new HashSet<CacheKey>(dependencyMapping.get(field)); // TODO is there a smarter way than copying the collection every time?
			for (CacheKey key : mappingCopy){
				if (cache.containsKey(key)) {
					cache.get(key).invalidate(key, fieldInvalidationTimeStamp);
				}
			}
			dependencyMapping.get(field).clear();
		}
		
		long endTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			endTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			endTime = System.nanoTime();
		}
		resetCounters(invalidations.incrementAndGet());
		resetCounters(totalInvalidationTime.addAndGet(endTime - startTime));
	}
	
	@Override
	public ReferenceQueue<? super Object> getReferenceQueue() {
		return deadReferences;
	}
	
	/**
	 * This implementation assumes that the {@link CacheKey} it is associated with is immutable or the arguments it is built from are.
	 * 
	 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
	 */
	private class CacheValueContainerImmutable extends AbstractCacheValueContainer{

		protected volatile Boolean keepStale; // set to true if an invalidation arrives while a value calculation is underway 
		private final boolean stable; // set to true if the stored value is the output of a stable @Cached method 
		
		CacheValueContainerImmutable(boolean stable){
			super();
			keepStale = false;
			this.stable = stable;
		}

		@Override
		public void invalidate(CacheKey thisKey, long fieldInvalidationTimeStamp) {
			switch (status){
			case FRESH:
				if (this.timeStamp <= fieldInvalidationTimeStamp) {
					synchronized (this) {
						staleObjects.incrementAndGet();
						status = CacheValueStatus.STALE;
						
						// recursively invalidate the parent values
						if(!stable) {
							for (CacheKey key : nestedDependencies) {
								if (cache.containsKey(key)) {
									cache.get(key).invalidate(key, fieldInvalidationTimeStamp);
								}
							}
							nestedDependencies.clear();
						}
						
						// brake the dependencies between this container and the fields that where read during the calculation of the current value
						for (FieldID field : fieldDependencies){
							dependencyMapping.get(field).remove(thisKey);
						}
						fieldDependencies.clear();
						
						// brake the dependencies between this container and the Collections (or Maps) that where read during the calculation of the current value
						for (Collection_Monitor monitor : collectionDependencies){
							monitor.removeDependency(thisKey);
						}
						collectionDependencies.clear();
						
						break;	
					}
				}
			case STALE:
				break;
			default: // STALE_RECALCULATING or EMPTY_CALCULATING
				if (this.timeStamp <= fieldInvalidationTimeStamp) {
					keepStale = true;
				}
				break;
			} 
		}

		public synchronized void update(Object value) {
			// if the value is stable no recursive invalidation happened when it was invalidated. Do it now (only if the new value is different from the old).
			if (stable && !value.equals(this.value)) {
				for (CacheKey key : nestedDependencies) {
					cache.get(key).invalidate(key, System.nanoTime());
				}
				nestedDependencies.clear();
			}
			
			this.value = value;
			if (keepStale) {
				if (status == CacheValueStatus.EMPTY_CALCULATING) {
					staleObjects.incrementAndGet();
				}
				status = CacheValueStatus.STALE;
			} else {
				if(status != CacheValueStatus.EMPTY_CALCULATING) {
					staleObjects.decrementAndGet();
				}
				status = CacheValueStatus.FRESH;
			}
			keepStale = false;
			notifyAll(); // notify all threads waiting for a fresh value
		}

		@Override
		public CacheFetch getOrCalculateIfStale(boolean waitForFresh) {
			registerNestedDependency();
			while(true){
				switch (status){
					case FRESH:
						return new CacheFetch(value, false);
					case STALE: // in this case the contract of this method requires the caller to calculate a new value
						synchronized (this) {
								if (status == CacheValueStatus.STALE) {
									status = CacheValueStatus.STALE_RECALCULATING;
									timeStamp = System.nanoTime();
									return new CacheFetch(null, true);
								}
						}
						break;
					default: // STALE_RECALCULATING or EMPTY_CALCULATING
						if (!waitForFresh) {
							if (status == CacheValueStatus.STALE_RECALCULATING) {
								return new CacheFetch(value, false);
							} else /* by exclusion this must be EMPTY_CALCULATING */ {
								
								/* the timeStamp is not updated even if a new calculation is starting to avoid
								 * the case in which the previous ongoing calculation will update the value and end up
								 * not being invalidated because the incoming invalidation timeStamp is between the
								 * original one and the one that would be set here
								 */
								return new CacheFetch(null, true);
							}
						}
						
						synchronized (this) {
							try {
								wait(); // a value is currently being calculated, wait for it
							} catch (InterruptedException e) {
								// TODO It's probably best to re-throw this and catch it in the Cache object and maybe simply re-invoke this method
								e.printStackTrace();
							}
							break;
						}
				}
			}
		}
		
	}

	/**
	 * This implementation assumes that it must recalculate itself automatically when it is invalidated. <br>
	 * The @Cached method calculating this values should not be declared "stable". <br>
	 * 
	 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
	 */
	private class CacheValueContainerAutoRecalc extends AbstractCacheValueContainer {

		private Object instance; // the object instance the @Cached method was invoked on. Null if method is static
		private Object[] args; // the arguments the @Cached method was invoked with
		private final Method method; // the @Cached method
		protected volatile Boolean keepStale; // set to true if an invalidation arrives while a value calculation is underway 
		
		CacheValueContainerAutoRecalc (Object instance, Method method, Object[] args, CacheKey thisKey){
			super();
			this.instance = instance;
			this.args = args;
			this.method = method;
			this.keepStale = false;
		}
		
		@Override
		public void invalidate(CacheKey thisKey, long fieldInvalidationTimeStamp) {
			switch (status){
			case FRESH:
				if (this.timeStamp <= fieldInvalidationTimeStamp) {
					synchronized (this) {
						staleObjects.incrementAndGet();
						status = CacheValueStatus.STALE;
						
						// brake the dependencies between this container and the fields that where read during the calculation of the current value
						for (FieldID field : fieldDependencies){
							dependencyMapping.get(field).remove(thisKey);
						}
						fieldDependencies.clear();

						// brake the dependencies between this container and the Collections (or Maps) that where read during the calculation of the current value
						for (Collection_Monitor monitor : collectionDependencies){
							monitor.removeDependency(thisKey);
						}
						collectionDependencies.clear();
						
						// self-recalculate!
						Recalculator.INSTANCE.addTask(new RecalculationTask(instance, method, args, thisKey));
					}
					break;	
				}
			case STALE:
				if (this.timeStamp <= fieldInvalidationTimeStamp) {
					// self-recalculate!
					Recalculator.INSTANCE.addTask(new RecalculationTask(instance, method, args, thisKey));
				}
				break;
			default: // STALE_RECALCULATING or EMPTY_CALCULATING
				if (this.timeStamp <= fieldInvalidationTimeStamp) {
					keepStale = true;
					
					// self-recalculate!
					Recalculator.INSTANCE.addTask(new RecalculationTask(instance, method, args, thisKey));
				}
				break;
			} 
		}
		
		public synchronized void update(Object newValue) {
			
			// recursively invalidate the parent values
			if(!newValue.equals(value)) {
				for (CacheKey key : nestedDependencies) {
					if (cache.containsKey(key)) {
						cache.get(key).invalidate(key, System.nanoTime());
					}
				}
				nestedDependencies.clear();
			}
			
			this.value = newValue;
			if (keepStale) {
				if (status == CacheValueStatus.EMPTY_CALCULATING) {
					staleObjects.incrementAndGet();
				}
				status = CacheValueStatus.STALE;
			} else {
				if(status != CacheValueStatus.EMPTY_CALCULATING) {
					staleObjects.decrementAndGet();
				}
				status = CacheValueStatus.FRESH;
			}
			keepStale = false;
			notifyAll(); // notify all threads waiting for a fresh value
		}
		
		@Override
		public CacheFetch getOrCalculateIfStale(boolean waitForFresh) {
			registerNestedDependency();
			while(true){
				switch (status){
					case FRESH:
						return new CacheFetch(value, false);
					case STALE: // in this case the contract of this method requires the caller to calculate a new value
						synchronized (this) {
							if (status == CacheValueStatus.STALE) {
								status = CacheValueStatus.STALE_RECALCULATING;
								timeStamp = System.nanoTime();
								return new CacheFetch(null, true);
							}
						}
						break;
					default: // STALE_RECALCULATING or EMPTY_CALCULATING
						
						// checks if it's a thread belonging to a Recalculator
						if (Thread.currentThread().getName() == "Recalculator") {
							return new CacheFetch(null, false); /* don't wait for a FRESH value and don't recalculate either. 
																   This will force the Recalculator to drop the calculation 
																   if there's already a "normal" one ongoing */
						}
												
						if (!waitForFresh) {
							if (status == CacheValueStatus.STALE_RECALCULATING) {
								return new CacheFetch(value, false);
							} else /* by exclusion this must be EMPTY_CALCULATING */ {
								
								/* the timeStamp is not updated even if a new calculation is starting to avoid
								 * the case in which the previous ongoing calculation will update the value and end up
								 * not being invalidated because the incoming invalidation timeStamp is between the
								 * original one and the one that would be set here
								 */
								return new CacheFetch(null, true);
							}
						}
						
						synchronized (this) {
							try {
								wait(); // a value is currently being calculated, wait for it
							} catch (InterruptedException e) {
								// TODO It's probably best to re-throw this and catch it in the Cache object and maybe simply re-invoke this method
								e.printStackTrace();
							}
							break;
						}
				}
			}
		}
		
	}
	
}