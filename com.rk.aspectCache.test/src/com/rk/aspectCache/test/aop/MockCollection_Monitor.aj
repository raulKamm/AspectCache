package com.rk.aspectCache.test.aop;

import com.rk.aspectCache.aop.Collection_Monitor;
import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.MonitoredCollection;
import com.rk.aspectCache.test.scaffolding.Exposer;

@TestAspect // to give its advice precedence over other aspect
public aspect MockCollection_Monitor extends Collection_Monitor {

	@Override
	public void iteratorRemove(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void iteratorNext(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDependency(CacheKey dependency) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long countDependencies() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	pointcut exposer(): call(* *.isEmpty(..)) && target(MonitoredCollection);
	before(): exposer() {
		Exposer.setCollection_Monitor(this);
	}
	
}
