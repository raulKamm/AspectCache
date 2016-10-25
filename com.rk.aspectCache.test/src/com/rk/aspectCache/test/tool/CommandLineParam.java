package com.rk.aspectCache.test.tool;

/**
 * This enum represent the command line arguments that can be passed to TestTool
 * 
 * @author Raul Bertone
 */

public enum CommandLineParam {
	 CALLER_N, // number of caller threads (positive int)
	 UPDATER_N, // number of updater threads (positive int)
	 DATA_SIZE, // number of elements in the collection that will be used as data (positive int)
	 RUNTIME, // in seconds (positive int)
	 CALL_RATE, // number of invocations per second per caller thread (positive int)
	 UPDATE_RATE, // number of invalidations per second per updater thread (positive int)
	 RANGE, // modifier to the (random) number of data elements read at each invocation by a caller thread (int)
	 METHOD // selects the set of properties specified for the @Cached annotation. See also Methods
}
