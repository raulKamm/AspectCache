package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sap.sailing.cache.common.CacheKey;
import com.sap.sailing.cache.common.CachedMethodSignature;
import com.sap.sailing.cache.common.DependencyThreadLocal;
import com.sap.sailing.cache.impl.IdentityCacheKey;
import com.sap.sailing.cache.test.scaffolding.Exposer;
import com.sap.sailing.cache.test.scaffolding.MockMethods;

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
