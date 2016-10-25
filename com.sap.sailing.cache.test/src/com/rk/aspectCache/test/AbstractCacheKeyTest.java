package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.MockAbstractCacheKey;
import com.rk.aspectCache.test.scaffolding.MockMethods;

public class AbstractCacheKeyTest {
	
	@BeforeClass
	public static void initializeJoinPoint(){
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
	}
	
	@Test
	public void testHashCode() {
		CachedMethodSignature signature = new CachedMethodSignature(Exposer.getSignature());
		
		assertEquals(new MockAbstractCacheKey(signature).hashCode(), signature.hashCode());
	}

	@Test
	public void testEquals() {
		CachedMethodSignature signature = new CachedMethodSignature(Exposer.getSignature());
	
		CacheKey key1 = new MockAbstractCacheKey(signature);
		CacheKey key2 = new MockAbstractCacheKey(signature);
		assertTrue(key1.equals(key2));
	}
}
