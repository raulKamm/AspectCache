package com.rk.aspectCache.test.aop;

import org.aspectj.lang.reflect.MethodSignature;

import com.rk.aspectCache.common.Cached;
import com.rk.aspectCache.test.scaffolding.Exposer;

@TestAspect //to give its advice precedence over other aspect's
public aspect MockCached_Method {

	pointcut CachedMethod(Object obj): execution(@Cached * *.*(..)) && this(obj) && !within(com.rk.aspectCache.test.tool.*); 
	pointcut StaticCachedMethod(): execution(@Cached static * *.*(..)) && !within(com.rk.aspectCache.test.tool.*);
	
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
