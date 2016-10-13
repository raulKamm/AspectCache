package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sap.sailing.cache.impl.InstanceFieldID;

public class InstanceFieldIDTest {

	private String field1 = "First";
	private String field2 = "Second";
	private Object obj1 = new Object();
	private Object obj2 = new Object();
	
	@Test
	public void sameObjectSameField() {
		InstanceFieldID id1 = new InstanceFieldID(obj1.hashCode(), field1);
		InstanceFieldID id2 = new InstanceFieldID(obj1.hashCode(), field1);
		assertTrue(id1.equals(id2));
	}
	
	@Test
	public void sameObjectDifferentField() {
		InstanceFieldID id1 = new InstanceFieldID(obj1.hashCode(), field1);
		InstanceFieldID id2 = new InstanceFieldID(obj1.hashCode(), field2);
		assertFalse(id1.equals(id2));
	}

	@Test
	public void differentObjectsSameField() {
		InstanceFieldID id1 = new InstanceFieldID(obj1.hashCode(), field1);
		InstanceFieldID id2 = new InstanceFieldID(obj2.hashCode(), field1);
		assertFalse(id1.equals(id2));
	}
	
	@Test
	public void differentObjectsDifferentFields() {
		InstanceFieldID id1 = new InstanceFieldID(obj1.hashCode(), field1);
		InstanceFieldID id2 = new InstanceFieldID(obj2.hashCode(), field2);
		assertFalse(id1.equals(id2));
	}
}

