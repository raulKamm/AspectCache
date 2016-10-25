package com.rk.aspectCache.impl;

import com.rk.aspectCache.common.MonitoringParameters;

public class CacheMonitor implements CacheMonitorMBean {

	private long totalHits;
	private long totalMisses;
	private long fieldDependencies;
	private long collectionDependencies;
	private int cacheEntries;
	
	@Override
	public long getAverageMissTime() {
		long misses = CacheImpl.INSTANCE.getCacheMisses();
		if (misses != 0){
			return CacheImpl.INSTANCE.getTotalMissTime() / (misses * 1000);
		} else {
			return 0;
		}
	}

	@Override
	public long getMisses() {
		totalMisses = CacheImpl.INSTANCE.getCacheMisses();
		return totalMisses;
	}
	
	@Override
	public long getAverageHitTime() {
		long hits = CacheImpl.INSTANCE.getCacheHits();
		if (hits != 0){
			return CacheImpl.INSTANCE.getTotalHitTime() / (hits * 1000);
		} else {
			return 0;
		}
	}

	@Override
	public long getHits() {
		totalHits = CacheImpl.INSTANCE.getCacheHits();
		return totalHits;
	}

	@Override
	public long getAverageInvalidationTime() {
		long invalidations = CacheImpl.INSTANCE.getTotalInvalidations();
		if (invalidations != 0){
			return CacheImpl.INSTANCE.getTotalInvalidationTime() / (invalidations * 1000);
		} else {
			return 0;
		}
	}
	
	@Override
	public long getInvalidations() {
		return CacheImpl.INSTANCE.getTotalInvalidations();
	}

	@Override
	public int getCacheEntriesNumber() {
		cacheEntries = CacheImpl.INSTANCE.getCacheEntriesNumber();
		return cacheEntries;
	}

	@Override
	public double getHitRatio() {
		return (double)totalHits / (double)(totalHits + totalMisses);
	}

	@Override
	public double getDependenciesPerEntry() {
		if (cacheEntries == 0) {
			return 0;
		} else {
			return (double)(fieldDependencies + collectionDependencies) / (double)cacheEntries;
		}
	}
	
	@Override
	public double getEntriesPerField() {
		int fields = CacheImpl.INSTANCE.getFieldNumber();
		if (fields == 0) {
			return 0;
		} else {
			return cacheEntries / CacheImpl.INSTANCE.getFieldNumber();
		}
	}

	@Override
	public long getCollectionDependencies() {
		collectionDependencies = MonitoringParameters.getCollectionDependencies();
		return collectionDependencies;
	}

	@Override
	public long getFieldDependencies() {
		fieldDependencies = CacheImpl.INSTANCE.getDependencies(); // field/CacheKeys plus CacheKey/fields dependencies
		return fieldDependencies;
	}
	
	@Override
	public long getTotalDependencies() {
		return fieldDependencies + collectionDependencies;
	}
	
	@Override
	public void clearStatistics() {
		CacheImpl.INSTANCE.resetCounters(-1);
	}

	@Override
	public int getUsedMemory() {
		return (int) ((fieldDependencies * 480) + (collectionDependencies * 80) + (cacheEntries * 1300));
	}

}
