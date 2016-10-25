package com.rk.aspectCache.aop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
 
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.MethodSignature;

import com.rk.aspectCache.common.Cache;
import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.Cached;
import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.common.MonitoringParameters;
import com.rk.aspectCache.impl.CacheFetch;
import com.rk.aspectCache.impl.CacheImpl;
import com.rk.aspectCache.impl.IdentityCacheKey;
import com.rk.aspectCache.impl.ImmutableCacheKey;
import com.rk.aspectCache.impl.MutableCacheKey;

/**
 * This aspect intercepts invocations to methods decorated with @Cached annotation. <br>
 * The invocation instance parameters and the method signature are used to build a {@link CacheKey} and a cache look-up is made: on a hit the value is simply returned
 * to the caller; on a miss the @Cached method is invoked and its output cached and returned to the caller.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */
@SuppressAjWarnings
public aspect Cached_Method{
	
	private final Cache cache = CacheImpl.INSTANCE;
	
	/** 
	 * matches all methods annotated with "@Cached", instance and static respectively
	 */
	pointcut CachedMethod(Object obj): execution(@Cached * *.*(..)) && this(obj); //TODO this can made faster creating different versions based on annotation values (stable, waitForFresh, etc.) thus avoiding unnecessary value lookups (all in here, no multiple aspects)
	pointcut StaticCachedMethod(): execution(@Cached static * *.*(..)); //TODO this can made faster creating different versions based on annotation values (stable, waitForFresh, etc.) thus avoiding unnecessary value lookups (all in here, no multiple aspects)
	
	Object around(Object obj): CachedMethod(obj) {
		
		// if CPU time monitoring is deactivated/non-available in this JVM, fallback using System.nanotime
		long startTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			startTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			startTime = System.nanoTime();
		}
		
		// key generation
		CacheKey key = null;
		MethodSignature signature = (MethodSignature) thisJoinPointStaticPart.getSignature(); 
		Cached annotation = signature.getMethod().getAnnotation(Cached.class);
		
		switch (annotation.keyType()) {
			case ARGS_IDENTITY: // arguments identity will be used to identify method invocation
				key = new IdentityCacheKey(new CachedMethodSignature(signature), thisJoinPoint.getArgs());
				break;
			case IMMUTABLE_ARGS: // the arguments are immutable so a cheap mutable key can be used
				key = new MutableCacheKey(new CachedMethodSignature(signature), thisJoinPoint.getArgs());
				break;
			case MUTABLE_ARGS: // the arguments are not immutable so an expensive immutable key must be used
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					ObjectOutputStream oosc = new ObjectOutputStream(baos);			
					oosc.writeObject(thisJoinPoint.getArgs());
					key = new ImmutableCacheKey(new CachedMethodSignature(signature), baos.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
		}

		// The @Cached elements values determine the fetch and calculation behavior
		CacheFetch response; // TODO if a calculation is due, the CacheKey stored in the cache should be used instead of the one just created. It could be used instead of the boolean "calculate" field in a CacheFetch.
		if (annotation.automaticRecalculation()) {
			response = cache.fetch(key, annotation.waitForFresh(), obj, signature.getMethod(), thisJoinPoint.getArgs());
		} else { 
			response = cache.fetch(key, annotation.waitForFresh(), annotation.stable());
		}
		
		if (response.calculate) {
			// Cache miss or stale
			DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(key);
			Object cacheValue = null;
			try {
				cacheValue = proceed(obj);
			} catch (RuntimeException e) {
				cacheValue = e;
				DependencyThreadLocal.INSTANCE.removeCacheElementCalculationID();
				cache.update(key, cacheValue);
				cache.addCacheMiss(startTime);
				throw e;
			}
			DependencyThreadLocal.INSTANCE.removeCacheElementCalculationID();
			cache.update(key, cacheValue);
			cache.addCacheMiss(startTime);
			return cacheValue;
		} else {
			// Cache hit
			cache.addCacheHit(startTime);
			if (response.value instanceof RuntimeException) {
				throw (RuntimeException) response.value;
			} else {
				return response.value;
			}
		}
	}
	
	Object around(): StaticCachedMethod() {
		
		// if CPU time monitoring is deactivated/non-available in this JVM, fallback using System.nanotime
		long startTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			startTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			startTime = System.nanoTime();
		}
		
		// key generation
		CacheKey key = null;
		MethodSignature signature = (MethodSignature) thisJoinPointStaticPart.getSignature(); 
		Cached annotation = signature.getMethod().getAnnotation(Cached.class);
		
		switch (annotation.keyType()) {
		case ARGS_IDENTITY: // arguments identity will be used to identify method invocation
			key = new IdentityCacheKey(new CachedMethodSignature(signature), thisJoinPoint.getArgs());
			break;
		case IMMUTABLE_ARGS: // the arguments are immutable so a cheap mutable key can be used
			key = new MutableCacheKey(new CachedMethodSignature(signature), thisJoinPoint.getArgs());
			break;
		case MUTABLE_ARGS: // the arguments are not immutable so an expensive immutable key must be used
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oosc = new ObjectOutputStream(baos);			
				oosc.writeObject(thisJoinPoint.getArgs());
				key = new ImmutableCacheKey(new CachedMethodSignature(signature), baos.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		// The @Cached elements values determine the fetch and calculation behavior
		CacheFetch response;
		if (annotation.automaticRecalculation()) {
			response = cache.fetch(key, annotation.waitForFresh(), null /* static method => no object instance */, signature.getMethod(), thisJoinPoint.getArgs());
		} else { 
			response = cache.fetch(key, annotation.waitForFresh(), annotation.stable());
		}
		
		if (response.calculate) {
			// Cache miss or stale
			DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(key);
			Object cacheValue = null;
			try {
				cacheValue = proceed();
			} catch (RuntimeException e) {
				cacheValue = e;
				DependencyThreadLocal.INSTANCE.removeCacheElementCalculationID();
				cache.update(key, cacheValue);
				cache.addCacheMiss(startTime);
				throw e;
			}
			DependencyThreadLocal.INSTANCE.removeCacheElementCalculationID();
			cache.update(key, cacheValue);
			cache.addCacheMiss(startTime);
			return cacheValue;
		} else {
			// Cache hit
			cache.addCacheHit(startTime);
			if (response.value instanceof RuntimeException) {
				throw (RuntimeException) response.value;
			} else {
				return response.value;
			}
		}
	}
	
}
