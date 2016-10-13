package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class IteratorWrapperTest {

	@Test
	public void test() {
		List<String> list = new LinkedList<String>();
		list.add("First");
		list.add("Second");
		list.add("Third");
		list.add("End");
		Iterator<String> iter = list.iterator();
		assertEquals("First", iter.next());
		assertEquals("Second", iter.next());
		iter.next();
		iter.remove();
		assertEquals("End", iter.next());
		assertFalse(iter.hasNext());
		assertEquals(3, list.size());
	}

}
