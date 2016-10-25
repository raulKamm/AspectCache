package com.rk.aspectCache.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.DependencyThreadLocal;

/**
 * When run, this class will invoke a @Cached method with the same arguments of a previous invocation (which result has been already cached and later invalidated).
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */
public class RecalculationTask implements Runnable {

	private final Object instance; // the object instance the @Cached method was invoked on. Null if method is static
	private final Object[] args; // the arguments the @Cached method was invoked with
	private final Method method; // the @Cached method
	private final CacheKey key; // the CacheKey associated with the @Cached method invocation
	
	public RecalculationTask (Object instance, Method method, Object[] args, CacheKey key){
		this.instance = instance;
		this.args = args;
		this.method = method;
		this.key = key;
	}
	
	@Override
	public void run() {
		try {
			DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(key);
			method.invoke(instance, args);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CacheKey getKey() {
		return key;
	}
}
