package com.sap.sailing.cache.impl;

import java.util.concurrent.ThreadFactory;

/**
 * Creates Threads for a Recalculator to use. <br>
 * The whole purpose of this is to name them so they can be recognized as belonging to a Recalculator (useful for example to prevent them waiting 
 * needlessly when querying the cache and finding a CALCULATING / RECALCULATING state).
 * 
 * @author Raul Bertone (D059912)
 */

public class RecalculatorThreadFactory implements ThreadFactory {

	private final ThreadGroup group;
	
	public RecalculatorThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }
	
	@Override
	public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, "Recalculator", 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
   }
	
}