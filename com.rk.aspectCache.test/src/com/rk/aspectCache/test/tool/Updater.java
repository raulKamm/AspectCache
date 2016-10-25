package com.rk.aspectCache.test.tool;

import java.util.Random;

import com.rk.aspectCache.common.IgnoreField;

public class Updater implements Runnable {
	
	@IgnoreField
	private boolean stop = false;
	@IgnoreField
	private DataCollection data;
	@IgnoreField
	private int updateRate;
	@IgnoreField
	private int dataSize;
	@IgnoreField
	private Random rnd;

	public Updater (DataCollection data, int callRate, int dataSize) {
		this.updateRate = callRate;
		this.data = data;
		this.dataSize = dataSize;
		this.rnd = new Random();
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	public void setUpdateRate(int callRate) {
		this.updateRate = callRate;
	}
	
	public int getUpdateRate() {
		return updateRate;
	}
	
	@Override
	public void run() {
		
		while(!stop){	
			try {
				Thread.sleep(1000 / updateRate);
				data.modify(rnd.nextInt(dataSize));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void blink() {
		stop = true;
	}
	
}
