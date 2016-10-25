package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import com.rk.aspectCache.impl.RecalculatorThreadFactory;

public class RecalculatorThreadFactoryTest {

	@Test
	public void testNewThread() {
		ThreadFactory factory = new RecalculatorThreadFactory();
		Thread t = factory.newThread(new Thread());
		assertEquals("Recalculator", t.getName());
	}

}
