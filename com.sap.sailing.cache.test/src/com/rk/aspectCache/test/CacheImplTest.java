package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.sailing.cache.common.CacheKey;
import com.sap.sailing.cache.common.CacheValueStatus;
import com.sap.sailing.cache.common.CachedMethodSignature;
import com.sap.sailing.cache.common.DependencyThreadLocal;
import com.sap.sailing.cache.impl.CacheFetch;
import com.sap.sailing.cache.impl.CacheImpl;
import com.sap.sailing.cache.impl.IdentityCacheKey;
import com.sap.sailing.cache.impl.InstanceFieldID;
import com.sap.sailing.cache.test.scaffolding.Exposer;
import com.sap.sailing.cache.test.scaffolding.MockMethods;

public class CacheImplTest {

	private static CachedMethodSignature signature;
	
	/* 
	 *  Creates a Signature, which is necessary to create CacheKeys which are used in most of these tests 
	 */
	@BeforeClass
	public static void createSignature(){
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
		if (DependencyThreadLocal.INSTANCE.getNestedDependency() != null) {
			DependencyThreadLocal.INSTANCE.removeCacheElementCalculationID();
		}
		signature = new CachedMethodSignature(Exposer.getSignature());
	}

	@Test
	public void testGetValueStatus() {
		Object[] args = new Object[1];
		args[0] = new Integer(10540);
		CacheKey key = new IdentityCacheKey(signature, args);
		assertEquals(CacheValueStatus.EMPTY, CacheImpl.INSTANCE.getValueStatus(key));
	}

	@Test
	public void testFetch() {
		Object[] args = new Object[1];
		args[0] = new Integer(15000);
		CacheKey key = new IdentityCacheKey(signature, args);
		
		CacheFetch cf = CacheImpl.INSTANCE.fetch(key, false, false);
		assertEquals(null, cf.value);
		assertEquals(true, cf.calculate);
	}

	@Test
	public void testFetchWithAutomaticRecalculation() {
		Object[] args = new Object[1];
		args[0] = new Integer(1077);
		CacheKey key = new IdentityCacheKey(signature, args);
		Method[] methods = Object.class.getMethods();
		CacheFetch cf = CacheImpl.INSTANCE.fetch(key, false, new Object(), methods[0], new Object[3]);
		assertEquals(null, cf.value);
		assertEquals(true, cf.calculate);
	}

	@Test
	public void testUpdate() {
		Object[] args = new Object[1];
		args[0] = new Integer(103200);
		CacheKey key = new IdentityCacheKey(signature, args);
		CacheImpl.INSTANCE.fetch(key, false, false);
		CacheImpl.INSTANCE.update(key, null);
		assertEquals(CacheValueStatus.FRESH, CacheImpl.INSTANCE.getValueStatus(key));
	}

	@Test
	public void testRegisterFieldDependency() {
		Object[] args = new Object[1];
		args[0] = new Integer(1200);
		CacheKey key = new IdentityCacheKey(signature, args);
		CacheImpl.INSTANCE.fetch(key, false, false);
		CacheImpl.INSTANCE.update(key, null);
		args[0] = new Integer(19200);
		CacheKey key2 = new IdentityCacheKey(signature, args);
		CacheImpl.INSTANCE.fetch(key2, false, false);
		CacheImpl.INSTANCE.update(key2, null);
		CacheImpl.INSTANCE.registerFieldDependency(new InstanceFieldID(444, "aField"), key);
		CacheImpl.INSTANCE.invalidateDataField(new InstanceFieldID(444, "aField"), System.nanoTime());
		assertEquals(CacheValueStatus.STALE, CacheImpl.INSTANCE.getValueStatus(key));
		assertEquals(CacheValueStatus.FRESH, CacheImpl.INSTANCE.getValueStatus(key2));
	}

	@Test
	public void testInvalidate() {
		Object[] args = new Object[1];
		args[0] = new Integer(10000);
		CacheKey key = new IdentityCacheKey(signature, args);
		CacheImpl.INSTANCE.fetch(key, false, false);
		CacheImpl.INSTANCE.update(key, null);
		HashSet<CacheKey> cacheKeySet = new HashSet<CacheKey>();
		cacheKeySet.add(key);
		CacheImpl.INSTANCE.invalidate(cacheKeySet, System.nanoTime());
		assertEquals(CacheValueStatus.STALE, CacheImpl.INSTANCE.getValueStatus(key));
	}

	@Test
	public void testInvalidateDataField() {
		Object[] args = new Object[1];
		args[0] = new Integer(12900);
		CacheKey key = new IdentityCacheKey(signature, args);
		CacheImpl.INSTANCE.fetch(key, false, false);
		CacheImpl.INSTANCE.update(key, null);
		CacheImpl.INSTANCE.registerFieldDependency(new InstanceFieldID(444, "aField"), key);
		CacheImpl.INSTANCE.invalidateDataField(new InstanceFieldID(444, "aField"), System.nanoTime());
		assertEquals(CacheValueStatus.STALE, CacheImpl.INSTANCE.getValueStatus(key));
	}

	@Test
	public void testInteractionSequence() {
		Object[] args = new Object[1];
		args[0] = new Integer(12200);
		CacheKey key = new IdentityCacheKey(signature, args);
		args[0] = new Integer(129900);
		CacheKey key2 = new IdentityCacheKey(signature, args);
		
		assertEquals(CacheValueStatus.EMPTY, CacheImpl.INSTANCE.getValueStatus(key));
		assertEquals(CacheValueStatus.EMPTY, CacheImpl.INSTANCE.getValueStatus(key2));
		CacheImpl.INSTANCE.fetch(key, false, false);
		CacheImpl.INSTANCE.fetch(key2, false, false);
		assertEquals(CacheValueStatus.EMPTY_CALCULATING, CacheImpl.INSTANCE.getValueStatus(key));
		CacheImpl.INSTANCE.update(key, null);
		CacheImpl.INSTANCE.update(key2, null);
		assertEquals(CacheValueStatus.FRESH, CacheImpl.INSTANCE.getValueStatus(key));
		HashSet<CacheKey> cacheKeySet = new HashSet<CacheKey>();
		cacheKeySet.add(key);
		cacheKeySet.add(key2);
		CacheImpl.INSTANCE.invalidate(cacheKeySet, System.nanoTime());
		assertEquals(CacheValueStatus.STALE, CacheImpl.INSTANCE.getValueStatus(key));
		assertEquals(CacheValueStatus.STALE, CacheImpl.INSTANCE.getValueStatus(key2));
		CacheImpl.INSTANCE.fetch(key, false, false);
		assertEquals(CacheValueStatus.STALE_RECALCULATING, CacheImpl.INSTANCE.getValueStatus(key));
		cacheKeySet.remove(key2);
		CacheImpl.INSTANCE.invalidate(cacheKeySet, System.nanoTime());
		assertEquals(CacheValueStatus.STALE_RECALCULATING, CacheImpl.INSTANCE.getValueStatus(key));
		CacheImpl.INSTANCE.update(key, null);
		assertEquals(CacheValueStatus.STALE, CacheImpl.INSTANCE.getValueStatus(key));
		
		CacheImpl.INSTANCE.update(key2, null);
		CacheImpl.INSTANCE.registerFieldDependency(new InstanceFieldID(444, "aField"), key2);
		CacheImpl.INSTANCE.invalidateDataField(new InstanceFieldID(444, "aField"), System.nanoTime());
		assertEquals(CacheValueStatus.STALE, CacheImpl.INSTANCE.getValueStatus(key2));
	}

}
