package com.appe.framework.job;

/**
 * Execution status to be used as FLOW CONTROL.
 * 
 * - An execution with status RETRY will be re-execute
 * - An execution with status WAIT will be in waiting mode until all children DONE
 * 
 * @author ho
 *
 */
public enum ExecutionStatus {
	SUCCESS,
	WARNING,
	FAIL,
	
	WAIT,
	RETRY;
	
	/**
	 * return true if it's a completed status
	 * @param status
	 * @return
	 */
	public static boolean isCompleted(ExecutionStatus status) {
		return (status == SUCCESS || status == WARNING || status == FAIL);
	}
}