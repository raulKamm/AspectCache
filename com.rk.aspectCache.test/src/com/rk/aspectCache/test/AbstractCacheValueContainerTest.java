package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import com.rk.aspectCache.aop.Collection_Monitor;
import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.CacheValueStatus;
import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.common.FieldID;
import com.rk.aspectCache.impl.InstanceFieldID;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.MockAbstractCacheKey;
import com.rk.aspectCache.test.scaffolding.MockAbstractCacheValueContainer;
import com.rk.aspectCache.test.scaffolding.MockMethods;
import com.rk.aspectCache.test.scaffolding.MockMonitoredCollection;

public class AbstractCacheValueContainerTest {
	
	private static MockAbstractCacheValueContainer container;
	
	@BeforeClass
	public static void initializeJoinPoint(){
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
		container = new MockAbstractCacheValueContainer();
	}
	
	@Test
	public void testRegistrationOfFieldDependency() {
		FieldID field = new InstanceFieldID(0, "a field");
		container.registerFieldDependency(field);
		assertTrue(container.checkFieldDependency(field));
	}
	
	@Test
	public void testRegistrationOfNestedDependency() {
		CachedMethodSignature signature = new CachedMethodSignature(Exposer.getSignature());
		CacheKey key = new MockAbstractCacheKey(signature);
		DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(key);
		container.registerNestedDependency();
		assertTrue(container.checkNestedDependency(key));
	}
	
	@Test
	public void testRegistrationOfCollectionDependency() {
		LinkedList<Integer> list = new MockMonitoredCollection<Integer>();
		list.isEmpty();
		Collection_Monitor monitor = Exposer.getCollection_Monitor();
		container.registerCollectionDependency(monitor);
		assertTrue(container.checkCollectionDependency(monitor));
	}
	
	@Test
	public void testStatusAfterInitialization() {
		assertEquals(CacheValueStatus.EMPTY_CALCULATING, container.getStatus());
	}

}
