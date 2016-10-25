package com.rk.aspectCache.aop;

import com.rk.aspectCache.common.Cached;
import com.rk.aspectCache.common.FieldMonitoringStrategy;
import com.rk.aspectCache.common.IgnoreField;
import com.rk.aspectCache.common.MonitoredTask;

/**
 * This aspect provides cache invalidation by instrumenting all (non-array type) fields. When a field "F" is read during the calculation of a cache element "E" (more correctly: within
 * the control flow of a method decorated with the @Cached annotation) it registers itself in E's dependencies. When F will then be modified, all cache elements depending
 * on it will be invalidated and F will be removed from their dependencies.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 *
 */
public aspect Field_Monitor_All extends Abstract_Field_Monitor{
	
	/**
	 * Pointcuts for the "monitor ALL" approach. 
	 * All field get happening within the control flow of a @Cached method and all field sets application-wide will be monitored. Field decorated with @IgnoreField annotation
	 * will be ignored both in get and set.
	 */
	// picks all calls to methods decorated with @Cached annotation or objects implementing the MonitoredTask interface.
	pointcut cachedMethod(): call(@Cached(fieldMonitoringStrategy = FieldMonitoringStrategy.ALL) * *(..)) || within(MonitoredTask+); 
	// picks all references to non-final, non-static, non-array type fields happening in the control flow of cachedMethod()
	pointcut dataFieldGet(Object obj): get(!@IgnoreField !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && cflow(cachedMethod()) && target(obj) && !within(com.rk.aspectCache.aop.*) && !within(com.rk.aspectCache.common.*) && !within(com.rk.aspectCache.impl.*);
	// picks all set of non-final, non-static, non-array type fields
	pointcut dataFieldSet(Object obj): set(!@IgnoreField !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && target(obj) && !within(com.rk.aspectCache.aop.*) && !within(com.rk.aspectCache.common.*) && !within(com.rk.aspectCache.impl.*);
	// picks all references to static, non-final, non-array type fields happening in the control flow of cachedMethod()
	pointcut dataFieldGetStatic(): get(!@IgnoreField static !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && cflow(cachedMethod()) && !within(com.rk.aspectCache.aop.*) && !within(com.rk.aspectCache.common.*) && !within(com.rk.aspectCache.impl.*);
	// picks all set of static, non-final, non-array type fields
	pointcut dataFieldSetStatic(): set(!@IgnoreField static !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && !within(com.rk.aspectCache.aop.*) && !within(com.rk.aspectCache.common.*) && !within(com.rk.aspectCache.impl.*);
	
}

