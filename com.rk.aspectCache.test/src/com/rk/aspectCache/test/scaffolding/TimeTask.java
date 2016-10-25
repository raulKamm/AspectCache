package com.rk.aspectCache.test.scaffolding;

import java.lang.reflect.Method;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.impl.RecalculationTask;
import com.rk.aspectCache.test.RecalculatorTest;

public class TimeTask extends RecalculationTask implements Runnable {

	private final int delay;
	private final CacheKey key;
	
	public TimeTask (Object instance, Method method, Object[] args, CacheKey key, int delay){
		super(instance, method, args, key);
		this.delay = delay;
		this.key = key;
	}
	
	@Override
	public void run() {
		RecalculatorTest.task(key);
		super.run();
		
		synchronized(this) {
			try {
				wait(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
