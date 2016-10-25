package com.rk.aspectCache.test.tool;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.rk.aspectCache.common.IgnoreField;

/**
 * An application for testing Autocache. 
 * The method that Autocache instruments and caches contains a very simple algorithm that runs in linear time
 * with very a little hidden constant so to avoid masking with its computation time the performance measurements
 * of Autocache. Also, the TestTool is designed to have a small memory footprint and not to produce any garbage,
 * again to simplify the measurements of the same dimensions for Autocache.
 * 
 * It can be configured at launch with command line options or at runtime through a MBean.
 * 
 * @author Raul Bertone
 */

public class TestTool {

    @IgnoreField
    private static final HashMap<String, CommandLineParam> parameters = getParameters();
    @IgnoreField
    private static final HashMap<String, Methods> methods = getMethods();
	
    @IgnoreField
	private static LinkedList<Caller> callers;
    @IgnoreField
    private static LinkedList<Updater> updaters;
    @IgnoreField
    private static DataCollection data;
    @IgnoreField
    private static int runtime = 100000;
    @IgnoreField
    private static AtomicLong totalTime = new AtomicLong(0);
    @IgnoreField
    private static AtomicLong calls = new AtomicLong(0);
    @IgnoreField
    private static int callRate = 100;
    @IgnoreField
    private static int updateRate = 1;
    @IgnoreField
    private static int range = 0;
	
	public static void main(String[] args) throws Exception {
		
		try {
			// set the parameters according to the command line arguments
			parseArgs(args);
			// register MBean
			registerMBean();
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return;
		}
		// run the simulation
		runSimulation();
	}

	private static void registerMBean() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName name = new ObjectName("com.rk.aspectCache.test.tool:type=CacheTest");
        TestToolManagement mbean = new TestToolManagement(); 
		mbs.registerMBean(mbean, name);
	}
	
	private static void runSimulation() {
		
		for (int j=0; j<callers.size(); j++) {
			new Thread(callers.get(j)).start();
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println("I've been interrupted!");
		}
		
		for (int j=0; j<updaters.size(); j++) {
			new Thread(updaters.get(j)).start();
		}
		
		try {
			Thread.sleep(runtime);
		} catch (InterruptedException e) {
			System.out.println("I've been interrupted!");
		}
		
		for (int j=0; j<callers.size(); j++) {
			callers.get(j).blink();
		}
		
		for (int j=0; j<updaters.size(); j++) {
			updaters.get(j).blink();
		}
	}
	
	private static void parseArgs(String[] args) {
		
		int numberOfCallers = 1;
		int numberOfUpdaters = 1;
		int dataSize = 100;
		Methods method = Methods.M_0000;
		
		boolean numberOfCallersFlag = false;
		boolean callRateFlag = false;
		boolean numberOfUpdatersFlag = false;
		boolean updateRateFlag = false;
		boolean dataSizeFlag = false;
		boolean rangeFlag = false;
		boolean runtimeFlag = false;
		boolean methodFlag = false;
		
		int i = 0;
		while (i < args.length) {
			CommandLineParam param = parameters.get(args[i]);
			
			switch (param) {
			case CALLER_N:
				if (numberOfCallersFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				numberOfCallers = Integer.parseInt(args[++i]);
				numberOfCallersFlag = true;
				i++;
				break;
			case CALL_RATE:
				if (callRateFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				callRate = Integer.parseInt(args[++i]);
				callRateFlag = true;
				i++;
				break;
			case DATA_SIZE:
				if (dataSizeFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				dataSize = Integer.parseInt(args[++i]);
				dataSizeFlag = true;
				i++;
				break;
			case RANGE:
				if (rangeFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				range = Integer.parseInt(args[++i]);
				rangeFlag = true;
				i++;
				break;
			case RUNTIME:
				if (runtimeFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				runtime = 1000 * Integer.parseInt(args[++i]);
				runtimeFlag = true;
				i++;
				break;
			case UPDATER_N:
				if (numberOfUpdatersFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				numberOfUpdaters = Integer.parseInt(args[++i]);
				numberOfUpdatersFlag = true;
				i++;
				break;
			case UPDATE_RATE:
				if (updateRateFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				updateRate = Integer.parseInt(args[++i]);
				updateRateFlag = true;
				i++;
				break;
			case METHOD:
				if (methodFlag) throw new IllegalArgumentException("repeated \"" + args[i] + "\" command.");
				method = methods.get(args[++i]);
				
				if (method == null) {
					throw new IllegalArgumentException("\"" + args[i] + "\" is not a recognized method.");
				}
				
				updateRateFlag = true;
				i++;
				break;
			default:
				throw new IllegalArgumentException("\"" + args[i] + "\" is not recognized.");
			}
		}
		
		prepareConfiguration(numberOfCallers, numberOfUpdaters, dataSize, method);
	}
	
	private static void prepareConfiguration(int numberOfCallers, int numberOfUpdaters, int dataSize, Methods method) {
		// set DataCollection
		data = new DataList(dataSize);
		PrimeFactors.setData(data);
		
		// set callers
		callers = new LinkedList<Caller>();
		for (int j=0; j<numberOfCallers; j++) {
			callers.add(new Caller(method, callRate, range, dataSize));
		}
		
		// set updaters
		updaters = new LinkedList<Updater>();
		for (int j=0; j<numberOfUpdaters; j++) {
			updaters.add(new Updater(data, updateRate, dataSize));
		}
	}
	
	private static HashMap<String, CommandLineParam> getParameters() {
        HashMap<String, CommandLineParam> ret = new HashMap<String, CommandLineParam>();
        ret.put("-callerN", CommandLineParam.CALLER_N);
        ret.put("-updaterN", CommandLineParam.UPDATER_N);
        ret.put("-callRate", CommandLineParam.CALL_RATE);
        ret.put("-updateRate", CommandLineParam.UPDATE_RATE);
        ret.put("-dataSize", CommandLineParam.DATA_SIZE);
        ret.put("-range", CommandLineParam.RANGE);
        ret.put("-runtime", CommandLineParam.RUNTIME);
        return ret;
    }
	
	private static HashMap<String, Methods> getMethods() {
        HashMap<String, Methods> ret = new HashMap<String, Methods>();
        ret.put("0000", Methods.M_0000);
        ret.put("0001", Methods.M_0001);
        ret.put("0010", Methods.M_0010);
        ret.put("0011", Methods.M_0011);
        ret.put("0100", Methods.M_0100);
        ret.put("0101", Methods.M_0101);
        ret.put("1000", Methods.M_1000);
        ret.put("1001", Methods.M_1001);
        ret.put("1010", Methods.M_1010);
        ret.put("1011", Methods.M_1011);
        ret.put("1100", Methods.M_1100);
        ret.put("1101", Methods.M_1101);
        return ret;
    }
	
	public static LinkedList<Caller> getCallers() {
		return callers;
	}

	public static LinkedList<Updater> getUpdaters() {
		return updaters;
	}
		
	public static DataCollection getDataCollection() {
		return data;
	}

	public static void setCallerNumber(int n) {
		int difference = callers.size() - n;
		
		// remove excess elements
		while (difference > 0) {
			callers.pop().blink();
			difference--;
		}
		
		// increase size to newSize
		while (difference < 0) {
			Caller cll = new Caller(Methods.M_0000, callRate, range, data.getSize());
			callers.add(cll);
			new Thread(cll).start();
			difference++;
		}
	}

	public static void setUpdaterNumber(int n) {
		int difference = updaters.size() - n;
		
		// remove excess elements
		while (difference > 0) {
			updaters.pop().blink();
			difference--;
		}
		
		// increase size to newSize
		while (difference < 0) {
			Updater up = new Updater(data, updateRate, data.getSize());
			updaters.add(up);
			new Thread(up).start();
			difference++;
		}
	}
	
	public static long getTotalTime() {
		return totalTime.get();
	}

	public static long getCalls() {
		return calls.get();
	}

	public static void addCall(long startTime) {
		
		resetCounters(calls.incrementAndGet());
		
		// if CPU time monitoring is deactivated/non-available in this JVM, fallback using System.nanotime
		long endTime;
		if(false /*MonitoringParameters.isThreadCpuTimeEnabled()*/) { 
			endTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		} else {
			endTime = System.nanoTime();
		}

		resetCounters(totalTime.addAndGet(endTime - startTime));
	}
	
	public static int getCallRate() {
		return callRate;
	}

	public static void setCallRate(int newCallRate) {
		TestTool.callRate = newCallRate;
		for (Caller callr: callers) {
			callr.setCallRate(callRate);
		}
	}

	public static int getUpdateRate() {
		return updateRate;
	}

	public static void setUpdateRate(int newUpdateRate) {
		TestTool.updateRate = newUpdateRate;
		for (Updater callr: updaters) {
			callr.setUpdateRate(updateRate);
		}
	}

	public static int getRange() {
		return range;
	}

	public static void setRange(int newRange) {
		TestTool.range = newRange;
		for (Caller cllr: callers) {
			cllr.setRange(newRange);
		}
	}

	private static void resetCounters(long i) {
		if (i < 0) {
			totalTime.set(0);
			calls.set(0);
		}
	}
	
}
