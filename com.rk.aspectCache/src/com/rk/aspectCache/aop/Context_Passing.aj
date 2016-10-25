package com.rk.aspectCache.aop;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import com.rk.aspectCache.common.CacheKey;
import com.rk.aspectCache.common.DependencyThreadLocal;
import com.rk.aspectCache.common.MonitoredTask;

/**
 * This aspect manages context passing when Executors or children Threads are used. <br>
 * When a Runnable (including Thread) or Callable that implements the MonitoredTask interface is instantiated, it will implicitly implement
 * the ContextPassing interface: this aspect will call methods on that interface to copy/paste the context (the CacheKey currently under calculation in the parent thread).
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 */
@SuppressAjWarnings
public aspect Context_Passing {
	
	// TODO directly implement methods and fields on MonitoredTask interface without introducing an additional one (ContextPassing)
	// interface, field and methods definitions
	public interface ContextPassing {};
	private CacheKey ContextPassing.contextKey;
	public void ContextPassing.setKey(CacheKey context) {
		contextKey = context;
	}
	public CacheKey ContextPassing.getKey() {
		return contextKey;
	}

	// instances that will implement the interface
	declare parents: MonitoredTask+ implements ContextPassing;
	
	// matches calls to all constructors in types that implement MonitoredTask interface
	pointcut copyContext(): call(MonitoredTask+.new(..));
	after() returning(MonitoredTask task): copyContext() {
		task.setKey(DependencyThreadLocal.INSTANCE.getDependency());
	}
	
	// matches executions of methods run() and call() defined in a type which implements MonitoredTask interface
	pointcut pasteContext(MonitoredTask task): (execution(* MonitoredTask+.run()) || execution(* MonitoredTask+.call())) && this(task);
	before(MonitoredTask task): pasteContext(task) {
		DependencyThreadLocal.INSTANCE.setCacheElementCalculationID(task.getKey());
	}
}