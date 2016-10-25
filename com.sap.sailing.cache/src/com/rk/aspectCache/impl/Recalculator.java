package com.sap.sailing.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sap.sailing.cache.common.CacheKey;

/**
 * To avoid queuing more than one instance of the same cache entry recalculation, this implementation of ThreadPoolExecutor relies on a directly managed task queue. <br>
 * Clients needing to submit a task must do it by accessing this Executor through its addTask() method. <br>
 * <br>
 * addTask() can "kickstart" the Executor's threads submitting tasks as long as there are available "slots" (idle threads). Afterwards, when a task is completed,
 * the Recalculator will try to fetch more tasks from the queue: if the queue is empty the thread will idle again, until the next "kick" by addTask().
 * 
 * @author Raul Bertone (D059912)
 */
public class Recalculator extends ThreadPoolExecutor {

	// singleton pattern
	public static final Recalculator INSTANCE = new Recalculator();
			
	private int availableSlots = 4; // available threads
	private final Map<CacheKey, Runnable> tasks; // associates CacheKeys to the tasks submitted to recalculate them 
	private final ConcurrentLinkedQueue<CacheKey> taskQueue; // the task queue
		
	private Recalculator () {
		super (4, 4, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<Runnable>(4), new RecalculatorThreadFactory()); // TODO read thread parameters from config file
		tasks = new HashMap<CacheKey, Runnable>();
		taskQueue = new ConcurrentLinkedQueue<CacheKey>();
	}
	
	@Override
	protected synchronized void afterExecute(Runnable r, Throwable t) {
		tasks.remove(((RecalculationTask)r).getKey());
		
		/*
		 * checks if the queue is empty. If it is this thread will idle, otherwise it will pop the first task and execute it
		 */		
		if (taskQueue.isEmpty()) {
			// no task left on the queue
			availableSlots++;
			
		} else {
			CacheKey key = taskQueue.poll();
			Runnable task = tasks.get(key);
			execute(task);
		}
	}
	
	public synchronized void addTask(RecalculationTask task) {
		CacheKey key = task.getKey();
		
		if (!tasks.containsKey(key)) {
			if (availableSlots > 0) {
				availableSlots--;
				tasks.put(key, task);
				execute(task);
			} else {
				taskQueue.add(key);
				tasks.put(key, task);
			}
		}
	}
	
}
