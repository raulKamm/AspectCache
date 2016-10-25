package com.sap.sailing.cache.common;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;

import com.sap.sailing.cache.aop.Collection_Monitor;

public class MonitoringParameters {

	private static boolean  isThreadCpuTimeEnabled = false;
	private static HashSet<WeakReference<Collection_Monitor>> collectionRegister = new HashSet<WeakReference<Collection_Monitor>>();
	private static ReferenceQueue<? extends WeakReference<Collection_Monitor>> deadReferences = new ReferenceQueue<WeakReference<Collection_Monitor>>();

	public static boolean isThreadCpuTimeEnabled() {
		return isThreadCpuTimeEnabled;
	}

	public static void setThreadCpuTimeEnabled(boolean isThreadCpuTimeEnabled) {
		MonitoringParameters.isThreadCpuTimeEnabled = isThreadCpuTimeEnabled;
	}
	
	public static void registerCollection(WeakReference<Collection_Monitor> collection) {
		collectionRegister.add(collection);
	}
	
	public static long getCollectionDependencies() {
		// remove from registerdCollections the dead weakReferences
		while (true) {
			Reference<? extends WeakReference<Collection_Monitor>> ref = deadReferences.poll();
			if(ref != null) {
				collectionRegister.remove(ref);
			} else {
				break;
			}
			
		}
		
		long dependencies = 0;
		for (WeakReference<Collection_Monitor> ref: collectionRegister) {
			dependencies += ref.get().countDependencies();
		}
		return dependencies;
	}
}
