package com.rk.aspectCache.common;

/**
 * This is a marker interface used on Runnables, Callables and Threads to allow AspectJ to recognize and instrument them.
 * When such types implement this interface, the dependency to the CacheKey under calculation in the Thread that instantiated them will be
 * carried on to the Thread that will run them.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */
public interface MonitoredTask {
	// just a marker interface
}
