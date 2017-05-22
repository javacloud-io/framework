package com.appe.framework.job;
/**
 * A unit of execution will be invoke by ExecutionManager when it's ready to be execute.
 * Mostly it's state less action, any states will be local within execution
 * 
 * @author ho
 *
 */
public interface ExecutionAction {
	/**
	 * Invoke when JOB is ready to execute, an execution return:
	 * 
	 * - (SUCCESS,WARNING,FAIL) then onCompleted will be invoke
	 * - RETRY: will be reschedule and retry at some point
	 * - WAIT:  need to wait for child jobs if any until they completed
	 * 
	 * @param executionContext
	 * @return
	 */
	public ExecutionStatus onExecute(ExecutionContext executionContext);
	
	/**
	 * When execution is DONE, then onCompleted will invoke to call back. 
	 * If return value if FALSE job will be re-schedule to execute AGAIN.
	 * 
	 * This will allow to implement complicated flow control such as LOOP.
	 * @param executionContext
	 * @return
	 */
	public boolean onCompleted(ExecutionContext executionContext);
}
