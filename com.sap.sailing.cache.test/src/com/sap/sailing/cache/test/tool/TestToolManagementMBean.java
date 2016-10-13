package com.sap.sailing.cache.test.tool;

/**
 * MBean interface for the runtime management of the TestTool
 * 
 * @author Raul Bertone
 */

public interface TestToolManagementMBean {

	public int getDataSize();
	public void setDataSize(int size);
	
	public int getRange();
	public void setRange(int newRange);

	public int getCallRate();
	public void setCallRate(int callRate);

	public int getUpdateRate();
	public void setUpdateRate(int updateRate);

	public int getCallers();
	public void setCallers(int i);

	public int getUpdaters();
	public void setUpdaters(int i);
	
	public long getAverageCallTime();
	
	public int getMaxEntries(); // the theoretical maximum number of different CacheKeys given the current dataSize and range
	
}
