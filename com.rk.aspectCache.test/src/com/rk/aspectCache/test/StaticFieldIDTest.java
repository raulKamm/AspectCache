package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.rk.aspectCache.impl.StaticFieldID;

public class StaticFieldIDTest {

	private String field1 = "First";
	private String field2 = "Second";
	private String class1 = "com.rk.package.class1";
	private String class2 = "com.rk.package.class2";
	
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
