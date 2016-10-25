package com.rk.aspectCache.aop;

import com.rk.aspectCache.common.Cached;
import com.rk.aspectCache.common.DataField;
import com.rk.aspectCache.common.FieldMonitoringStrategy;
import com.rk.aspectCache.common.MonitoredTask;

/**
 * This aspect provides cache invalidation by instrumenting all (non-array type) fields. When a field "F" is read during the calculation of a cache element "E" (more correctly: within
 * the control flow of a method decorated with the @Cached annotation) it registers itself in E's dependencies. When F will then be modified, all cache elements depending
 * on it will be invalidated and F will be removed from their dependencies.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 *
 */
public aspect Field_Monitor_DataField extends Abstract_Field_Monitor{
	
	/**
	 * Pointcuts for the "monitor ANNOTATED_FIELDS_ONLY" approach.
	 * Only the fields decorated with @DataField will be monitored: all gets happening within the control flow of a @Cached method and all sets of those fields will be monitored.
	 */
	// picks all calls to methods decorated with @Cached annotation or objects implementing the MonitoredTask interface
	pointcut cachedMethod(): call(@Cached(fieldMonitoringStrategy = FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY) * *(..)) || within(MonitoredTask+); 
	// picks all references to non-final, non-static, non-array type fields happening in the control flow of cachedMethod()
	pointcut dataFieldGet(Object obj): get(@DataField !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && cflow(cachedMethod()) && target(obj) && !within(com.rk.aspectCache..*);
	// picks all set of non-final, non-static, non-array type fields
	pointcut dataFieldSet(Object obj): set(@DataField !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && target(obj) && !within(com.rk.aspectCache..*);
	// picks all references to static, non-final, non-array type fields happening in the control flow of cachedMethod()
	pointcut dataFieldGetStatic(): get(@DataField static !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && cflow(cachedMethod()) && !within(com.rk.aspectCache..*);
	// picks all set of static, non-final, non-array type fields
	pointcut dataFieldSetStatic(): set(@DataField static !final (!(Object+[]) && !(int[])) && !(byte[]) && !(short[]) && !(long[]) && !(float[]) && !(double[]) && !(boolean[]) && !(char[]) *) && !within(com.rk.aspectCache..*);

}

