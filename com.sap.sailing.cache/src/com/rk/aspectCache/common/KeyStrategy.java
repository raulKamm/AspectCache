package com.sap.sailing.cache.common;

/**
 * Indicates which type of key should be used
 * 
 * @author Raul Bertone (D059912)
 */

public enum KeyStrategy {
	
	IMMUTABLE_ARGS, // the arguments of the @Cached method are assumed to be immutable
	MUTABLE_ARGS, // the arguments of the @Cached method are assumed to be mutable
	ARGS_IDENTITY // for reference types arguments only their identity is taken into consideration
}
