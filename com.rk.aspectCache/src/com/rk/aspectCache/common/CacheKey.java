package com.rk.aspectCache.common;

/**
 * It identifies a specific @Cached method invocation and through its hashCode() and equals() methods it must be possible to 
 * distinguish not only which @Cached method created it but also the deep object graph of the instance parameters used in the method invocation.
 *  
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */

public interface CacheKey {

	// here to force overriding in implementing classes
	public int hashCode();
	
	// here to force overriding in implementing classes
	public boolean equals(Object obj);
	
	// to identify the type of key at runtime
	public boolean isMutable();
}
