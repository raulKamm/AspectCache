package com.rk.aspectCache.test.scaffolding;

import com.rk.aspectCache.common.Cached;

public class MockMethods {
	
	@Cached
	public static int staticOnePrimitiveParameter(int i){
		return i;
	}
	
	@Cached
	public static String staticOneReferenceParameter(Object o){
		return o.toString();
	}
	
	@Cached
	public static Object staticSomeMixedParameters(int i, Object o, String[] s){
		return o;
	}
	
	@Cached
	public Object instanceSomeMixedParameters(int i, Object o, String[] s){
		return o;
	}
	
	@Cached
	public int instanceOnePrimitiveParameter(int i){
		return i;
	}
	
	@Cached
	public String instanceOneReferenceParameter(Object o){
		return o.toString();
	}

}
