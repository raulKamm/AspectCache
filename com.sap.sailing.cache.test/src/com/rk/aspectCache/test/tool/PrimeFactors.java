package com.sap.sailing.cache.test.tool;

import java.math.BigInteger;

import com.sap.sailing.cache.common.Cached;
import com.sap.sailing.cache.common.FieldMonitoringStrategy;
import com.sap.sailing.cache.common.IgnoreField;

public class PrimeFactors {
	
	@IgnoreField 
	private static DataCollection data;
	
	public static void setData(DataCollection data) {
		PrimeFactors.data = data;
	}
	
	/*
	 * The methods reads some values from the dataCollection and adds them up.
	 */
	private static BigInteger primeFactors(int first, int last) {
		BigInteger sum = BigInteger.valueOf(0);
		
		for (int i=first; i<=last; i++) {
			sum.add(BigInteger.valueOf(data.getValue(i).getPosition()));
		}
   		
   		return sum;
	}
	
	@Cached
	public static BigInteger primeFactors0000(int first, int last) {
		return primeFactors(first, last);
	}
	 
	@Cached (fieldMonitoringStrategy = FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY)
	public static BigInteger primeFactors0001(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (automaticRecalculation = true)
	public static BigInteger primeFactors0010(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (fieldMonitoringStrategy = FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY,
			 automaticRecalculation = true)
	public static BigInteger primeFactors0011(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (stable = true)
	public static BigInteger primeFactors0100(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (fieldMonitoringStrategy = FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY,
			 stable = true)
	public static BigInteger primeFactors0101(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (waitForFresh = true)
	public static BigInteger primeFactors1000(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (waitForFresh = true,
			fieldMonitoringStrategy = FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY)
	public static BigInteger primeFactors1001(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (waitForFresh = true,
			automaticRecalculation = true)
	public static BigInteger primeFactors1010(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (waitForFresh = true,
			automaticRecalculation = true,
			fieldMonitoringStrategy = FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY)
	public static BigInteger primeFactors1011(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (waitForFresh = true,
			stable = true)
	public static BigInteger primeFactors1100(int first, int last) {
		return primeFactors(first, last);
	}
	
	@Cached (waitForFresh = true,
			fieldMonitoringStrategy = FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY,
			stable = true)
	public static BigInteger primeFactors1101(int first, int last) {
		return primeFactors(first, last);
	}
	
}
