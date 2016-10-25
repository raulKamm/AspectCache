package com.sap.sailing.cache.common;

/**
 * Every element represent a different monitoring strategy.
 * 
 * @author Raul Bertone (D059912)
 */

public enum FieldMonitoringStrategy {

	ALL, // will monitor all fields application-wide (and beyond: including third party library and Java Runtime API)
	ANNOTATED_FIELDS_ONLY // will monitor only those fields that have been decorated with @DataField annotation
}
