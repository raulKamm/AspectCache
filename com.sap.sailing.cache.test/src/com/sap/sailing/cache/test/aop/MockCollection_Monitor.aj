package com.sap.sailing.cache.test.aop;

import com.sap.sailing.cache.aop.Collection_Monitor;
import com.sap.sailing.cache.common.CacheKey;
import com.sap.sailing.cache.common.MonitoredCollection;
import com.sap.sailing.cache.test.scaffolding.Exposer;

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
