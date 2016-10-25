package com.rk.aspectCache.test.scaffolding;

import org.aspectj.lang.reflect.MethodSignature;

import com.rk.aspectCache.aop.Collection_Monitor;


/**
 * Obtaining a reference to an aspect is difficult. They cannot be instantiated (only the AspectJ runtime can) so the aspect instance itself <br>
 * must provide a reference: it does so by copying it here, where it can be statically retrieved by the unit tests which need it. <br><br>
 * 
 * This class also provides access to other AspectJ runtime objects that are "difficult to reach": for example the Signature of a JoinPoint.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */
public class Exposer {

	private static MethodSignature signature;
	private static Collection_Monitor monitor;
	
	public static void setSignature(MethodSignature signature){
		Exposer.signature = signature;
	}
	
	public static MethodSignature getSignature(){
		return signature;
	}
	
	public static void setCollection_Monitor(Collection_Monitor monitor){
		Exposer.monitor = monitor;
	}
	
	public static Collection_Monitor getCollection_Monitor(){
		return monitor;
	}
	
}
