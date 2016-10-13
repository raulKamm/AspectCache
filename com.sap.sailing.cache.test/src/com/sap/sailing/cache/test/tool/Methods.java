package com.sap.sailing.cache.test.tool;

public enum Methods {
	M_0000, // default options
	M_0001, // FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY
	M_0010, // automaticRecalculation = true
	M_0011, // FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY, automaticRecalculation = true
	M_0100, // stable = true
	M_0101, // FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY, stable = true
	M_1000, // waitForFresh = true
	M_1001, // waitForFresh = true, FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY
	M_1010, // waitForFresh = true, automaticRecalculation = true
	M_1011, // waitForFresh = true, automaticRecalculation = true, FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY
	M_1100, // waitForFresh = true, stable = true
	M_1101; // waitForFresh = true, FieldMonitoringStrategy.ANNOTATED_FIELDS_ONLY, stable = true
}
