package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;

import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.MockMethods;

public class CachedMethodSignatureTest {
	
	private static final String declaringTypeName = "com.rk.aspectCache.test.scaffolding.MockMethods";
	
	@Test
	public void testInstanceOnePrimitiveParameter() {
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
		
		
		MethodSignature signature = Exposer.getSignature();
		CachedMethodSignature cms = new CachedMethodSignature(signature);
		String[] s = new String[1]; // parameters
		s[0] = "int";
		System.out.println(signature.getName() + signature.getParameterTypes().toString() + signature.getDeclaringTypeName());
		assertEquals(calculateHash("instanceOnePrimitiveParameter", "int", declaringTypeName, s), cms.hashCode());
	}
	
	@Test
	public void testInstanceOneReferenceParameter() {
		MockMethods mm = new MockMethods();
		mm.instanceOneReferenceParameter("forty two");
		
		MethodSignature signature = Exposer.getSignature();
		CachedMethodSignature cms = new CachedMethodSignature(signature);
		String[] s = new String[1]; // parameters
		s[0] = "java.lang.Object";
		assertEquals(calculateHash("instanceOneReferenceParameter", "java.lang.String", declaringTypeName, s), cms.hashCode());
	}
	
	@Test
	public void testInstanceSomeMixedParameters() {
		MockMethods mm = new MockMethods();
		mm.instanceSomeMixedParameters(42, mm, null);
		
		MethodSignature signature = Exposer.getSignature();
		CachedMethodSignature cms = new CachedMethodSignature(signature);
		String[] s = new String[3]; // parameters
		s[0] = "int";
		s[1] = "java.lang.Object";
		s[2] = "[Ljava.lang.String;";
		assertEquals(calculateHash("instanceSomeMixedParameters", "java.lang.Object", declaringTypeName, s), cms.hashCode());
	}
	
	@Test
	public void testStaticOnePrimitiveParameter() {
		MockMethods.staticOnePrimitiveParameter(42);
		
		MethodSignature signature = Exposer.getSignature();
		CachedMethodSignature cms = new CachedMethodSignature(signature);
		String[] s = new String[1]; // parameters
		s[0] = "int";
		assertEquals(calculateHash("staticOnePrimitiveParameter", "int", declaringTypeName, s), cms.hashCode());
	}
	
	@Test
	public void testStaticOneReferenceParameter() {
		MockMethods.staticOneReferenceParameter("forty two");
		
		MethodSignature signature = Exposer.getSignature();
		CachedMethodSignature cms = new CachedMethodSignature(signature);
		String[] s = new String[1]; // parameters
		s[0] = "java.lang.Object";
		assertEquals(calculateHash("staticOneReferenceParameter", "java.lang.String", declaringTypeName, s), cms.hashCode());
	}
	
	@Test
	public void testStaticSomeMixedParameters() {
		MockMethods.staticSomeMixedParameters(42, null, null);
		
		MethodSignature signature = Exposer.getSignature();
		CachedMethodSignature cms = new CachedMethodSignature(signature);
		String[] s = new String[3]; // parameters
		s[0] = "int";
		s[1] = "java.lang.Object";
		s[2] = "[Ljava.lang.String;";
		assertEquals(calculateHash("staticSomeMixedParameters", "java.lang.Object", declaringTypeName, s), cms.hashCode());
	}
	
	@Test
	public void testEquals(){
		MockMethods.staticOnePrimitiveParameter(42);
		MethodSignature signature = Exposer.getSignature();
		CachedMethodSignature cms1 = new CachedMethodSignature(signature);
		CachedMethodSignature cms2 = new CachedMethodSignature(signature);
		assertTrue(cms1.equals(cms2));
		
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
		signature = Exposer.getSignature();
		CachedMethodSignature cms3 = new CachedMethodSignature(signature);
		assertTrue(!cms1.equals(cms3));
	}
	
	private int calculateHash(String name, String returnTypeName, String declaringTypeName, String[] parameterTypeNames){
		
		int hashValue = 0;
		int prime = 31;
		
		for (String str : parameterTypeNames){
			hashValue = prime * hashValue + str.hashCode();
		}
		
		hashValue = prime * hashValue + name.hashCode();
		hashValue = prime * hashValue + returnTypeName.hashCode();
		hashValue = prime * hashValue + declaringTypeName.hashCode();
		
		return hashValue;
	}

}