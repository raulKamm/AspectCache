package com.sap.sailing.cache.impl;

import java.util.ListIterator;

import com.sap.sailing.cache.aop.List_Monitor;

/**
 * This wrapper explicitly maintains trace of the current state of its wrapped ListIterator (like cursor's position, direction of the last cursor movement, etc.). <br>
 * Whenever the backing Collection is accessed or modified through the wrapped Iterator, this wrapper informs the List_Monitor aspect monitoring
 * the backing Collection so that dependencies and invalidations on the Collection are properly handled even if generated indirectly.
 * 
 * @author Raul Bertone (D059912)
 *
 * @param <E> The generic type of the wrapped Iterator
 */

public class ListIteratorWrapper<E> implements ListIterator<E> {

	private final List_Monitor aspect; // the aspect monitoring the Collection the wrapped Iterator is based upon
	private final ListIterator<E> itr; // the wrapped Iterator
	private int cursor = 0; // the current position of the wrapped Iterator's
	private boolean lastCallWasToNext; // set to true by a call to next(), set to false by a call to previous()
	
	public ListIteratorWrapper(ListIterator<E> itr, List_Monitor aspect) {
		this.aspect = aspect;
		this.itr = itr;
	}

	public ListIteratorWrapper(ListIterator<E> itr, List_Monitor aspect, int index) {
		this(itr, aspect);
		cursor = index;
	}

	// always remember: first forward the call to the wrapped object and only later modify the wrapper state, in case an exception is thrown 
	
	@Override
	public void add(E e) {
		itr.add(e);
		aspect.iteratorAdd(cursor);
		cursor++; // elements are added before the current cursor position, so its value must be increased
		
	}

	@Override
	public boolean hasNext() {
		return itr.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return itr.hasPrevious();
	}

	@Override
	public E next() {
		E element = itr.next();
		aspect.iteratorNext(cursor);
		cursor++;
		lastCallWasToNext = true;
		return element;
	}

	@Override
	public int nextIndex() {
		return itr.nextIndex();
	}

	@Override
	public E previous() {
		E element = itr.previous();
		aspect.iteratorNext(cursor - 1);
		cursor--;
		lastCallWasToNext = false;
		return element;
	}

	@Override
	public int previousIndex() {
		return itr.previousIndex();
	}

	@Override
	public void remove() {
		itr.remove();
		if (lastCallWasToNext) { // the last element returned by a call to either next() or previous() will be removed
			cursor--;
		}
		aspect.iteratorRemove(cursor);
	}

	@Override
	public void set(E e) {
		itr.set(e);
		if (lastCallWasToNext) { // the last element returned by a call to either next() or previous() will be set
			aspect.iteratorSet(cursor - 1);
		} else {
			aspect.iteratorSet(cursor);
		}
	}

}
