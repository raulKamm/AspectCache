package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sap.sailing.cache.common.CachedMethodSignature;
import com.sap.sailing.cache.impl.ArgsWeakReference;
import com.sap.sailing.cache.impl.CacheImpl;
import com.sap.sailing.cache.impl.IdentityCacheKey;
import com.sap.sailing.cache.test.scaffolding.Exposer;
import com.sap.sailing.cache.test.scaffolding.MockMethods;

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
