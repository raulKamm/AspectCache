package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.sailing.cache.aop.Collection_Monitor;
import com.sap.sailing.cache.common.CacheKey;
import com.sap.sailing.cache.common.CacheValueStatus;
import com.sap.sailing.cache.common.CachedMethodSignature;
import com.sap.sailing.cache.common.DependencyThreadLocal;
import com.sap.sailing.cache.common.FieldID;
import com.sap.sailing.cache.impl.InstanceFieldID;
import com.sap.sailing.cache.test.scaffolding.Exposer;
import com.sap.sailing.cache.test.scaffolding.MockAbstractCacheKey;
import com.sap.sailing.cache.test.scaffolding.MockAbstractCacheValueContainer;
import com.sap.sailing.cache.test.scaffolding.MockMethods;
import com.sap.sailing.cache.test.scaffolding.MockMonitoredCollection;

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
