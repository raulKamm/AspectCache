package com.sap.sailing.cache.impl;

/**
 * This Mbean exposes Autocache's runtime statistics. 
 * 
 * @author Raul Bertone
 */

public interface CacheMonitorMBean {
	
	public long getAverageMissTime();
	public long getMisses();
	
	public long getAverageHitTime();
	public long getHits();
	
	public double getHitRatio();
	
	public long getAverageInvalidationTime();
	public long getInvalidations();
	
	public long getTotalDependencies();
	
	public int getCacheEntriesNumber();
	
	public double getDependenciesPerEntry();
	public double getEntriesPerField();
	
	public long getCollectionDependencies();
	public long getFieldDependencies();
	
	public int getUsedMemory();

	/**
	 * Clears the values of all non-instantaneous metrics (those that are averaged over a period of time)
	 */
	public void clearStatistics();
}
