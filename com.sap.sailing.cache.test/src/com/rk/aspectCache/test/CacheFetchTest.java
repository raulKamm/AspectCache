package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.rk.aspectCache.impl.CacheFetch;
import com.rk.aspectCache.test.scaffolding.CacheFetchConstructor;

public class CacheFetchTest {

	@Test
	public void test() {
		Integer i = new Integer(8);
		CacheFetch cf = new CacheFetchConstructor(i, false);
		assertEquals(i, cf.value);
		assertEquals(false, cf.calculate);
	}

}
