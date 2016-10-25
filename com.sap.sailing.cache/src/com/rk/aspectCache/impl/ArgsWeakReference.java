package com.sap.sailing.cache.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import com.sap.sailing.cache.common.CacheKey;

/**
 * A WeakReference where is possible to specify an owning CacheKey. <br>
 * When it will be found in a ReferenceQueue, it will be possible to identify the owner.
 * 
 * @author Raul Bertone (D059912)
 * @param <T>
 */

public class ArgsWeakReference<T> extends WeakReference<T> {

	private final CacheKey owner; // the CacheKey this Reference is a member of
	
	public ArgsWeakReference (T referent, ReferenceQueue<? super T> q, CacheKey owner) {
		super(referent, q);
		assert (owner != null);
		this.owner = owner;
	}
	
	public CacheKey getOwner() {
		return owner;
	}
}
