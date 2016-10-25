package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;

import com.sap.sailing.cache.common.CacheKey;
import com.sap.sailing.cache.common.CachedMethodSignature;
import com.sap.sailing.cache.test.scaffolding.Exposer;
import com.sap.sailing.cache.test.scaffolding.MockAbstractCacheKey;
import com.sap.sailing.cache.test.scaffolding.MockMethods;

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
