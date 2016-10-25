package com.rk.aspectCache.test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import com.rk.aspectCache.common.CachedMethodSignature;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.impl.IdentityCacheKey;
import com.rk.aspectCache.impl.RecalculationTask;
import com.rk.aspectCache.test.scaffolding.Exposer;
import com.rk.aspectCache.test.scaffolding.GetterSetter;
import com.rk.aspectCache.test.scaffolding.MockMethods;

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
