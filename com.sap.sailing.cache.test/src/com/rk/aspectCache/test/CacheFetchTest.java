package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sap.sailing.cache.impl.CacheFetch;
import com.sap.sailing.cache.test.scaffolding.CacheFetchConstructor;

public class CacheFetchTest {

	@Test
	public void test() {
		Integer i = new Integer(8);
		CacheFetch cf = new CacheFetchConstructor(i, false);
		assertEquals(i, cf.value);
		assertEquals(false, cf.calculate);
	}

}
