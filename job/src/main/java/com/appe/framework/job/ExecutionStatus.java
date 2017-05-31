package com.appe.framework.job;

/**
 * 
 * @author ho
 *
 */
public enum ExecutionStatus {
	SUCCESS,
	WARNING,
	FAIL,
	
	CANCEL,
	
	WAIT,
	RETRY;
	
	//return true if it's a completed status
	public static boolean isCompleted(ExecutionStatus status) {
		return (status == SUCCESS || status == WARNING || status == FAIL);
	}
}
