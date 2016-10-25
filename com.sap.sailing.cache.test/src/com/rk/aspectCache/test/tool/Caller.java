package com.sap.sailing.cache.test.tool;

import java.lang.management.ManagementFactory;
import java.util.Random;

import com.sap.sailing.cache.common.IgnoreField;

public class Caller implements Runnable {
	
	@IgnoreField
	private boolean stop = false;
	@IgnoreField
	private Methods method;
	@IgnoreField
	private int callRate;
	@IgnoreField
	private int range;
	@IgnoreField
	private int dataSize;
	@IgnoreField
	private Random rnd;
	
	public Caller (Methods method, int callRate, int range, int dataSize) {
		this.method = method;
		this.callRate = callRate;
		this.range = range;
		this.dataSize = dataSize;
		this.rnd = new Random();
	}
	
	public void setCallRate(int callRate) {
		this.callRate = callRate;
	}
	
	public int getCallRate() {
		return callRate;
	}
	
	public int getRange() {
		return range;
	}
	
	public void setRange(int newRange){
		range = newRange;
	}
	
	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	@Override
	public void run() {
		
		int first;
		int last;
		
		while(!stop){
			
				try {
					first = rnd.nextInt(dataSize);
					last = rnd.nextInt(dataSize);
				 	
				 	// make sure first <= last
				 	if (first > last) {
				 		int sup = last;
				 		last = first;
				 		first = sup;
				 	}
				 	
				    // modify the number of data elements that will be read according to the range parameter
			 		int rest = range - first;
			 		if (rest > 0) {
			 			first = 0;
			 			if ((last + rest) < dataSize) {
			 				last = last + rest;
			 			} else {
			 				last = dataSize - 1;
			 			}
			 		} else {
			 			first = first - range;
			 		}
					
			 		// invoke the cached method
				 	call(first, last);
				 	
					Thread.sleep(1000 / callRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void call(int first, int last) {
		// if CPU time monitoring is deactivated/non-available in this JVM, fallback using System.nanotime
		long startTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			startTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			startTime = System.nanoTime();
		}
		
		switch (method) {
			case M_0000: 
				PrimeFactors.primeFactors0000(first, last);
				break;
			case M_0001: 
				PrimeFactors.primeFactors0001(first, last);
				break;
			case M_0010: 
				PrimeFactors.primeFactors0010(first, last);
				break;
			case M_0011: 
				PrimeFactors.primeFactors0011(first, last);
				break;
			case M_0100: 
				PrimeFactors.primeFactors0100(first, last);
				break;
			case M_0101: 
				PrimeFactors.primeFactors0101(first, last);
				break;
			case M_1000: 
				PrimeFactors.primeFactors1000(first, last);
				break;
			case M_1001: 
				PrimeFactors.primeFactors1001(first, last);
				break;
			case M_1010:
				PrimeFactors.primeFactors1010(first, last);
				break;
			case M_1011:
				PrimeFactors.primeFactors1011(first, last);
				break;
			case M_1100:
				PrimeFactors.primeFactors1100(first, last);
				break;
			case M_1101: 
				PrimeFactors.primeFactors1101(first, last);
				break;
		}
				
		TestTool.addCall(startTime); // only relevant when the TestTool is used without Autocache.
									 // Otherwise the times recorded by the latter are more accurate
	}
	
	public void blink(){
		stop = true;
	}
}
