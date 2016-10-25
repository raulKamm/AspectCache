package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import java.util.ListIterator;

import org.junit.BeforeClass;
import org.junit.Test;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.impl.ListIteratorWrapper;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.MockAbstractCacheKey;
import com.rk.aspectCache.test.scaffolding.MockMethods;
import com.rk.aspectCache.test.scaffolding.MonitoredLinkedList;

public class ListIteratorWrapperTest {

	@BeforeClass
	public static void prepare(){
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
		CachedMethodSignature signature = new CachedMethodSignature(Exposer.getSignature());
		CacheKey key = new MockAbstractCacheKey(signature);
		DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(key);
	}
	
	@Test
	public void test() {
		MonitoredLinkedList<String> list = new MonitoredLinkedList<String>();
		list.add("First");
		list.add("Second");
		list.add("Third");
		list.add("End");
		ListIterator<String> iter = list.listIterator();
		assertTrue(iter instanceof ListIteratorWrapper);
		
		assertEquals("First", iter.next());
		assertTrue(iter.hasPrevious());
		iter.remove();
		assertFalse(iter.hasPrevious());
		assertEquals("Second", iter.next());
		assertEquals(0, iter.previousIndex());
		assertEquals(1, iter.nextIndex());
		iter.next();
		iter.next();
		iter.previous();
		assertEquals("End", iter.next());
		iter.set("Changed");
		assertEquals("Changed", list.get(2));
		assertFalse(iter.hasNext());
		assertEquals(3, list.size());
		iter.add("Added");
		assertEquals(4, list.size());
	}

}
