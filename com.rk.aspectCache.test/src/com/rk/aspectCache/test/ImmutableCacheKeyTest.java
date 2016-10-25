package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.impl.ImmutableCacheKey;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.MockMethods;

public class ImmutableCacheKeyTest {

	private static CachedMethodSignature signature1;
	private static CachedMethodSignature signature1bis; 
	private static CachedMethodSignature signature2;
	private static CachedMethodSignature signature3;
	
	@BeforeClass
	public static void createSignatures(){
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
		signature1 = new CachedMethodSignature(Exposer.getSignature());
		mm.instanceOnePrimitiveParameter(43);
		signature1bis = new CachedMethodSignature(Exposer.getSignature());
		MockMethods.staticOnePrimitiveParameter(42);
		signature2 = new CachedMethodSignature(Exposer.getSignature());
		mm.instanceSomeMixedParameters(44, null, null);
		signature3 = new CachedMethodSignature(Exposer.getSignature());
	}
	
	private String serialize(Object[] args) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oosc = new ObjectOutputStream(baos);			
			oosc.writeObject(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baos.toString();
	}
	
	@Test
	public void test() {
		
		// same method, same arguments (one primitive parameter)
		Object[] args1 = new Object[1];
		args1[0] = new Integer(10000);
		CacheKey key1 = new ImmutableCacheKey(signature1, serialize(args1));
		
		Object[] args1bis = new Object[1];
		args1bis[0] = new Integer(10000);
		CacheKey key1bis = new ImmutableCacheKey(signature1, serialize(args1bis));
		assertTrue(key1.equals(key1bis));
		
		// same method, same arguments, different method signature
		key1bis = new ImmutableCacheKey(signature1bis, serialize(args1bis));
		assertTrue(key1.equals(key1bis));
		
		// different methods, same arguments (one primitive parameter)
		Object[] args2 = new Object[1];
		args2[0] = new Integer(10000);
		CacheKey key2 = new ImmutableCacheKey(signature2, serialize(args2));
		assertFalse(key1.equals(key2));
		
		// same method, different arguments (one primitive parameter)
		CacheKey key3 = new ImmutableCacheKey(signature1, serialize(args1));
		Object[] args4 = new Object[1];
		args4[0] = new Integer(42);
		CacheKey key4 = new ImmutableCacheKey(signature1, serialize(args4));
		assertFalse(key3.equals(key4));
		
		// same method, same value arguments (mixed parameters)
		int i = 5;
		Integer r = new Integer(7);
		String[] str1 = new String[2];
		str1[0] = "hallo";
		str1[1] = "ciao";
		Object[] args5 = new Object[3];
		args5[0] = i;
		args5[1] = r;
		args5[2] = str1;
		CacheKey key5 = new ImmutableCacheKey(signature3, serialize(args5));
		int j = 5;
		Integer s = new Integer(7);
		String[] str2 = new String[2];
		str2[0] = "hallo";
		str2[1] = "ciao";
		Object[] args6 = new Object[3];
		args6[0] = j;
		args6[1] = s;
		args6[2] = str2;
		CacheKey key5bis = new ImmutableCacheKey(signature3, serialize(args6));
		assertTrue(key5.equals(key5bis));
		
		// same method, identical arguments (mixed parameters)
		CacheKey key5ter = new ImmutableCacheKey(signature3, serialize(args5));	
		assertTrue(key5.equals(key5ter));		
	}
	
}
