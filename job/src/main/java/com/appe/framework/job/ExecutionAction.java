package com.appe.framework.job;

import java.util.Set;

/**
 * A unit of execution will be invoke by ExecutionManager when it's ready to be execute.
 * Mostly it's state less action, any states will be local within execution
 * 
 * @author ho
 *
 */
public interface ExecutionAction {
	/**
	 * Parameters to feed into JOB, T is simple type
	 */
	public interface Parameters {
		/**
		 * return READONLY keys
		 * 
		 * @return
		 */
		public Set<String> keys();
		
		/**
		 * 
		 * @param key
		 * @return
		 */
		public <T> T get(String key);
	}
	
	/**
	 * Attributes to persist state across RETRY
	 */
	public interface Attributes extends Parameters {
		/**
		 * 
		 * @param key
		 * @param value
		 */
		public <T> void set(String key, T value);
	}
	
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
	public boolean onCompletion(ExecutionContext executionContext);
}
