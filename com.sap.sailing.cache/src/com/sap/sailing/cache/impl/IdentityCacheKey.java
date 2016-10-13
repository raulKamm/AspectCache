package com.sap.sailing.cache.impl;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.sap.sailing.cache.common.AbstractCacheKey;
import com.sap.sailing.cache.common.CachedMethodSignature;

/**
 * This CacheKey is built using the identity of the reference arguments of a cached method invocation. <br>
 * More specifically, for types passed by value - i.e. primitive types - their value is used, while for types passed by reference
 * their identity is used instead. An exception is made for String, which being immutable for this purpose can be treated as a primitive type.
 * 
 * @author Raul Bertone (D059912)
 */

public class IdentityCacheKey extends AbstractCacheKey {

	private final Vector<Object> args = new Vector<Object>(); // the arguments used in the cached method invocation represented by this CacheKey
	private final int hashCode; // the cached hash value
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
	
	public IdentityCacheKey(CachedMethodSignature signature, Object[] args) {
		
		super(signature);
		int hashValue = 0;

		if (args != null) {
			// TODO if the wrapped values are extracted and saved as primitive types they will be immutable allowing to relax the requirements on wrappers
			for (Object obj: args) {
				if (WRAPPER_TYPES.contains(obj.getClass())) {
					this.args.add(obj);
					hashValue = 31 * hashValue + obj.hashCode();
				} else {
					this.args.add(new ArgsWeakReference<Object>(obj, CacheImpl.INSTANCE.getReferenceQueue(), this));
					hashValue = 31 * hashValue + System.identityHashCode(obj);
				}
			}
		}
		hashCode = hashValue;
	}

	private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class); // not a wrapper but OK because Strings are immutable
        return ret;
    }

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentityCacheKey other = (IdentityCacheKey) obj;
		if (args.size() != other.args.size())
			return false;
			
		for (int i=0; i<args.size(); i++) { // this kind of brakes the equals() contract because it only checks for identity not equality
			if (WRAPPER_TYPES.contains(args.elementAt(i).getClass())) {
				if (!args.elementAt(i).equals(other.args.elementAt(i)))
					return false;					
			} else {
				if (((WeakReference<?>)args.elementAt(i)).get() != ((WeakReference<?>)other.args.elementAt(i)).get())
					return false;
			}
		}

		return true;
	}

}
