package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import com.sap.sailing.cache.common.CachedMethodSignature;
import com.sap.sailing.cache.common.DependencyThreadLocal;
import com.sap.sailing.cache.impl.IdentityCacheKey;
import com.sap.sailing.cache.impl.RecalculationTask;
import com.sap.sailing.cache.test.scaffolding.Exposer;
import com.sap.sailing.cache.test.scaffolding.GetterSetter;
import com.sap.sailing.cache.test.scaffolding.MockMethods;

public class RecalculationTaskTest {

	@Test
	public void test() {
		GetterSetter gs = new GetterSetter();
		gs.setValue(9);
		Method[] methods = gs.getClass().getMethods();
		Object[] args = new Object[1];
		args[0] = 42;
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(7);
		IdentityCacheKey key = new IdentityCacheKey(new CachedMethodSignature(Exposer.getSignature()), /*no arguments*/ null);
		RecalculationTask task = new RecalculationTask(gs, methods[5], args, key); // quando sistemero' il weave path a build path,
																				// e quindi gs non verra' piu' per qualche ragione weaved da List_Monitor, il numero del methods non sara' piu' "5"
		
		Thread t = new Thread(task);
		t.run();

		assertEquals(key, DependencyThreadLocal.INSTANCE.getDependency());
		assertEquals(42, gs.getValue());
	}

}
