package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.impl.ArgsWeakReference;
import com.rk.aspectCache.impl.CacheImpl;
import com.rk.aspectCache.impl.IdentityCacheKey;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.MockMethods;

public class ArgsWeakReferenceTest {

	@Before
	public void initializeJoinPoinExposer(){
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
	}
	
	@Test
	public void testGetOwner() {
		// returns owner
		IdentityCacheKey key = new IdentityCacheKey(new CachedMethodSignature(Exposer.getSignature()), /*no arguments*/ null);
		ArgsWeakReference<Object> ref2 = new ArgsWeakReference<Object>(new String("referent"), CacheImpl.INSTANCE.getReferenceQueue(),
																	   key);
		assertEquals(key, ref2.getOwner());
	}

}
