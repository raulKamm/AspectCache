package com.rk.aspectCache.test.tool;

public class TestToolManagement implements TestToolManagementMBean{
	
	@Override
	public int getCallRate() {
		return TestTool.getCallRate();
	}

	@Override
	public void setCallRate(int newCallRate) {
		TestTool.setCallRate(newCallRate);
	}

	@Override
	public int getUpdateRate() {
		return TestTool.getUpdateRate();		
	}

	@Override
	public void setUpdateRate(int newUpdateRate) {
		TestTool.setUpdateRate(newUpdateRate);
	}

	@Override
	public int getDataSize() {
		return TestTool.getDataCollection().getSize();
	}

	@Override
	public void setDataSize(int size) {
		
		if (size > TestTool.getDataCollection().getSize()) {
			TestTool.getDataCollection().setSize(size);
			
			for (Updater upd: TestTool.getUpdaters()) {
				upd.setDataSize(size);
			}
			
			for (Caller cllr: TestTool.getCallers()) {
				cllr.setDataSize(size);
			}
			
		} else {
			for (Updater upd: TestTool.getUpdaters()) {
				upd.setDataSize(size);
			}
			
			for (Caller cllr: TestTool.getCallers()) {
				cllr.setDataSize(size);
			}
			
			TestTool.getDataCollection().setSize(size);
		}
		
	}

	@Override
	public int getRange() {
		return TestTool.getRange();
	}

	@Override
	public void setRange(int newRange) {
		TestTool.setRange(newRange);
	}

	@Override
	public int getCallers() {
		return TestTool.getCallers().size();
	}

	@Override
	public void setCallers(int n) {
		TestTool.setCallerNumber(n);
	}

	@Override
	public int getUpdaters() {
		return TestTool.getUpdaters().size();
	}

	@Override
	public void setUpdaters(int n) {
		TestTool.setUpdaterNumber(n);
	}

	@Override
	public long getAverageCallTime() {
		long calls = TestTool.getCalls();
		if (calls != 0){
			return TestTool.getTotalTime() / (calls * 1000);
		} else {
			return 0;
		}
	}

	@Override
	public int getMaxEntries() {
		int range = TestTool.getRange();
		int dataSize = TestTool.getDataCollection().getSize();
		
		int n = 0;
		for (int i=1; i<=dataSize; i++) {
			int z;
			if ((range + i) < dataSize) {
				z = range + i;
			} else {
				z = dataSize;
			}

			for (int j=0; j<=(dataSize-z); j++) {
				n++;
			}
		}
		return n;
	}
	
}
