package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sap.sailing.cache.impl.StaticFieldID;

public class StaticFieldIDTest {

	private String field1 = "First";
	private String field2 = "Second";
	private String class1 = "com.sap.sailig.package.class1";
	private String class2 = "com.sap.sailig.package.class2";
	
	@Test
	public void sameClassSameField() {
		StaticFieldID id1 = new StaticFieldID(class1, field1);
		StaticFieldID id2 = new StaticFieldID(class1, field1);
		assertTrue(id1.equals(id2));
	}
	
	@Test
	public void sameClassDifferentField() {
		StaticFieldID id1 = new StaticFieldID(class1, field1);
		StaticFieldID id2 = new StaticFieldID(class1, field2);
		assertFalse(id1.equals(id2));
	}

	@Test
	public void differentClassesSameField() {
		StaticFieldID id1 = new StaticFieldID(class1, field1);
		StaticFieldID id2 = new StaticFieldID(class2, field1);
		assertFalse(id1.equals(id2));
	}
	
	@Test
	public void differentClassesDifferentFields() {
		StaticFieldID id1 = new StaticFieldID(class1, field1);
		StaticFieldID id2 = new StaticFieldID(class2, field2);
		assertFalse(id1.equals(id2));
	}
	
}
