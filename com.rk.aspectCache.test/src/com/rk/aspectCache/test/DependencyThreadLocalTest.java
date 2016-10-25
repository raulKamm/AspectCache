package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.impl.IdentityCacheKey;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.MockMethods;

public class DependencyThreadLocalTest {

	@Test
	public void testDependencyThreadLocal() {
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);		
		
		CacheKey key1 = new IdentityCacheKey(new CachedMethodSignature(Exposer.getSignature()), null);
		CacheKey key2 = new IdentityCacheKey(new CachedMethodSignature(Exposer.getSignature()), null);
		
		// initially empty
		assertNull(DependencyThreadLocal.INSTANCE.getNestedDependency());
		DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(key1);
		// returns the first element
		assertEquals(key1, DependencyThreadLocal.INSTANCE.getDependency());
		assertEquals(key1, DependencyThreadLocal.INSTANCE.getNestedDependency());
		DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(key2);
		// returns the second element
		assertEquals(key2, DependencyThreadLocal.INSTANCE.getDependency());
		assertEquals(key2, DependencyThreadLocal.INSTANCE.getNestedDependency());
		DependencyThreadLocal.INSTANCE.removeCacheElementCalculationID();
		// returns the first element
		assertEquals(key1, DependencyThreadLocal.INSTANCE.getDependency());
		assertEquals(key1, DependencyThreadLocal.INSTANCE.getNestedDependency());
		DependencyThreadLocal.INSTANCE.removeCacheElementCalculationID();
		// empty
		assertNull(DependencyThreadLocal.INSTANCE.getDependency());
		assertNull(DependencyThreadLocal.INSTANCE.getNestedDependency());
	}

}
