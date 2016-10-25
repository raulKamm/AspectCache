package com.sap.sailing.cache.common;

public enum CacheValueStatus {
	
	/**
	 * Note that EMPTY is used rather inconsistently: while the other values describe the state of an existing {@link CacheValueContainer},
	 * EMPTY means that the object doesn't exist at all.
	 */
	STALE, // at least one of the fields that has been read during the calculation of the current value has been modified
	FRESH, // all the data the value has been calculated from is still actual
	STALE_RECALCULATING, // like stale, but the calculation of an updated value is currently underway
	EMPTY_CALCULATING, // no value is present yet, but the calculation of one is currently underway
	EMPTY // the value (and its wrapping CacheValueContainer) don't exist
}
