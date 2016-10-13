package com.sap.sailing.cache.test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;

import com.sap.sailing.cache.common.CacheKey;
import com.sap.sailing.cache.common.CachedMethodSignature;
import com.sap.sailing.cache.impl.Recalculator;
import com.sap.sailing.cache.impl.IdentityCacheKey;
import com.sap.sailing.cache.test.scaffolding.Exposer;
import com.sap.sailing.cache.test.scaffolding.MockMethods;
import com.sap.sailing.cache.test.scaffolding.TimeTask;

public class RecalculatorTest {

	private static LinkedList<CacheKey> completedTasks = new LinkedList<CacheKey>();
	
	public static void task(CacheKey i) {
		completedTasks.add(i);
	}
	
	private void stagger(int wait) {
		synchronized(this) {
			try {	
				wait(wait);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void test() {
		MockMethods mm = new MockMethods();
		mm.instanceOnePrimitiveParameter(42);
		CachedMethodSignature signature = new CachedMethodSignature(Exposer.getSignature());
		MethodSignature method = Exposer.getSignature();
		
		Object[] args1 = new Object[1];
		args1[0] = new Integer(10000);
		CacheKey key1 = new IdentityCacheKey(signature, args1);
		TimeTask task1 = new TimeTask(mm, method.getMethod(), args1, key1, 2000);
		
		Object[] args2 = new Object[1];
		args2[0] = new Integer(10001);
		CacheKey key2 = new IdentityCacheKey(signature, args2);
		TimeTask task2 = new TimeTask(mm, method.getMethod(), args2, key2, 2000);
		 
		Object[] args3 = new Object[1];
		args3[0] = new Integer(10002);
		CacheKey key3 = new IdentityCacheKey(signature, args3);
		TimeTask task3 = new TimeTask(mm, method.getMethod(), args3, key3, 2000);
		
		Object[] args4 = new Object[1];
		args4[0] = new Integer(10003);
		CacheKey key4 = new IdentityCacheKey(signature, args4);
		TimeTask task4 = new TimeTask(mm, method.getMethod(), args4, key4, 2000);
		
		Object[] args5 = new Object[1];
		args5[0] = new Integer(10004);
		CacheKey key5 = new IdentityCacheKey(signature, args5);
		TimeTask task5 = new TimeTask(mm, method.getMethod(), args5, key5, 2000);
		
		Object[] args6 = new Object[1];
		args6[0] = new Integer(10005);
		CacheKey key6 = new IdentityCacheKey(signature, args6);
		TimeTask task6 = new TimeTask(mm, method.getMethod(), args6, key6, 2000);
			
		Recalculator.INSTANCE.addTask(task1);
		stagger(50);
		Recalculator.INSTANCE.addTask(task2);
		stagger(50);
		Recalculator.INSTANCE.addTask(task3);
		stagger(50);
		Recalculator.INSTANCE.addTask(task4);
		stagger(50);
		Recalculator.INSTANCE.addTask(task2);
		stagger(50);
		Recalculator.INSTANCE.addTask(task4);
		stagger(50);
		Recalculator.INSTANCE.addTask(task5);
		stagger(50);
		Recalculator.INSTANCE.addTask(task6);
		stagger(50);
		Recalculator.INSTANCE.addTask(task5);
		stagger(50);
		Recalculator.INSTANCE.addTask(task1);
		
		stagger(4000);
		
		Recalculator.INSTANCE.addTask(task6);
		stagger(50);
		Recalculator.INSTANCE.addTask(task1);
		
		stagger(3000);
		
		LinkedList<CacheKey> taskSequence = new LinkedList<CacheKey>();
		taskSequence.add(key1);
		taskSequence.add(key2);
		taskSequence.add(key3);
		taskSequence.add(key4);
		taskSequence.add(key5);
		taskSequence.add(key6);
		taskSequence.add(key6);
		taskSequence.add(key1);
		assertEquals(taskSequence, completedTasks);
	}

}
