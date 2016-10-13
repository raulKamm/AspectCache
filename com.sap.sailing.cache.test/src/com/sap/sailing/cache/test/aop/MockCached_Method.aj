package com.sap.sailing.cache.test.aop;

import org.aspectj.lang.reflect.MethodSignature;

import com.sap.sailing.cache.common.Cached;
import com.sap.sailing.cache.test.scaffolding.Exposer;

@TestAspect //to give its advice precedence over other aspect's
public aspect MockCached_Method {

	pointcut CachedMethod(Object obj): execution(@Cached * *.*(..)) && this(obj) && !within(com.sap.sailing.cache.test.tool.*); 
	pointcut StaticCachedMethod(): execution(@Cached static * *.*(..)) && !within(com.sap.sailing.cache.test.tool.*);
	
	Object around(Object obj): CachedMethod(obj) {
		
		MethodSignature signature = (MethodSignature) thisJoinPointStaticPart.getSignature();
		Exposer.setSignature(signature);
		
		return null;
	}
	
	Object around(): StaticCachedMethod() {
		
		MethodSignature signature = (MethodSignature) thisJoinPointStaticPart.getSignature();
		Exposer.setSignature(signature);
		
		return null;
	}
}
