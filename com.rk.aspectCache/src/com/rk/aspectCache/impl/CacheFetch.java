package com.rk.aspectCache.impl;

/**
 * Wraps the result of a cache lookup.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

public class CacheFetch {
	
	public final Object value; // the cache value returned by this fetch
	public final boolean calculate; // tells the calling @Cached method if it is supposed to proceed with a new calculation or if "value" is fresh and can be consumed
	
	protected CacheFetch(Object value, boolean calculate) {
		this.value = value;
		this.calculate = calculate;
	}
}