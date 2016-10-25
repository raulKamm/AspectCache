package com.rk.aspectCache.impl;

import java.util.Iterator;

import com.rk.aspectCache.aop.Collection_Monitor;

/**
 * This wrapper explicitly maintains trace of the current state of its wrapped Iterator (cursor's position). <br>
 * Whenever the underlying Collection is accessed or modified through the wrapped Iterator, this wrapper informs the Collection_Monitor aspect monitoring
 * the underlying Collection so that dependencies and invalidations on the Collection are properly handled even if generated indirectly.
 * 
 * @author Raul Bertone (raul.bertone@emptyingthebuffer.com)
 *
 * @param <E> The generic type of the wrapped Iterator
 */

public class IteratorWrapper<E> implements Iterator<E> {

	private final Collection_Monitor aspect; // the aspect monitoring the Collection backing the wrapped Iterator
	private final Iterator<E> itr; // the wrapped Iterator
	
	private int cursor = 0; // index of next element to return
	
	public IteratorWrapper(Iterator<E> itr, Collection_Monitor aspect) {
		this.aspect = aspect;
		this.itr = itr;
	}

	// always remember: first forward the call to the wrapped object and only afterwards modify the wrapper state, in case an exception is thrown 
	
	@Override
	public boolean hasNext() {
		return itr.hasNext();
	}

	@Override
	public E next() {
		E element = itr.next();
		aspect.iteratorNext(cursor);
		cursor++;
		return element;
	}

	@Override
	public void remove() {
		itr.remove();
		cursor--;
		aspect.iteratorRemove(cursor);
	}

}
