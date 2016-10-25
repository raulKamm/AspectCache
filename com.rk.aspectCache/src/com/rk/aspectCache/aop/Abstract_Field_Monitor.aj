package com.rk.aspectCache.aop;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import com.rk.aspectCache.common.Cache;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.common.FieldID;
import com.rk.aspectCache.impl.CacheImpl;
import com.rk.aspectCache.impl.InstanceFieldID;
import com.rk.aspectCache.impl.StaticFieldID;

/**
 * This aspect provides cache invalidation by instrumenting all (non-array type) fields. When a field "F" is read during the calculation of a cache element "E" (more correctly: within
 * the control flow of a method decorated with the @Cached annotation) it registers itself in E's dependencies. When F will then be modified, all cache elements depending
 * on it will be invalidated and F will be removed from their dependencies. <br>
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 *
 */
@SuppressAjWarnings
public abstract aspect Abstract_Field_Monitor{
	 
	private final Cache cache = CacheImpl.INSTANCE;
	
	abstract pointcut cachedMethod();
	abstract pointcut dataFieldGet(Object obj);
	abstract pointcut dataFieldSet(Object obj);
	abstract pointcut dataFieldGetStatic(); 
	abstract pointcut dataFieldSetStatic();

	/**
	 * After a field is read, it adds itself to the dependencies of the cache entry currently under calculation
	 * 
	 * @param obj The object of which the field is a member
	 */
	
	after(Object obj) returning: dataFieldGet(obj) {
		FieldID field = new InstanceFieldID (System.identityHashCode(obj), thisJoinPointStaticPart.getSignature().getName());
		cache.registerFieldDependency(field, DependencyThreadLocal.INSTANCE.getDependency());
	}
	
	after() returning: dataFieldGetStatic() {
		FieldID field = new StaticFieldID (thisJoinPointStaticPart.getSignature().getDeclaringTypeName(), thisJoinPointStaticPart.getSignature().getName());
		cache.registerFieldDependency(field, DependencyThreadLocal.INSTANCE.getDependency());
	}
	
	/**
	 * After a field is set, it fires an invalidation
	 * 
	 * @param obj The object of which the field is a member
	 */
	after(Object obj) returning: dataFieldSet(obj) {
		FieldID field = new InstanceFieldID (System.identityHashCode(obj), thisJoinPointStaticPart.getSignature().getName());
		cache.invalidateDataField(field, System.nanoTime());
	}

	after() returning: dataFieldSetStatic() {
		FieldID field = new StaticFieldID (thisJoinPointStaticPart.getSignature().getDeclaringTypeName(), thisJoinPointStaticPart.getSignature().getName());
		cache.invalidateDataField(field, System.nanoTime());
	}
}

