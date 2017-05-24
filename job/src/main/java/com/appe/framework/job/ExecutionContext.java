package com.appe.framework.job;

import java.util.List;

/**
 * Job execution will have context parameters/attributes with NAME/VALUE
 * 
 * @author ho
 *
 */
public interface ExecutionContext {
	/**
	 * return UNIQUE execution ID if it still alive
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * return NAME of execution action
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * return the current status of the JOB is finish otherwise NULL
	 */
	public ExecutionState.Status getStatus();
	
	/**
	 * return run id if being executed more than ONE. in case of RETRY/LOOP
	 * @return
	 */
	public int getRetryCount();
	
	/**
	 * return current context parameters
	 * @return
	 */
	public ExecutionState.Parameters getParameters();
	
	/**
	 * return the current context attributes
	 * 
	 * @return
	 */
	public ExecutionState.Attributes getAttributes();
	
	/**
	 * return all children jobs or jobs with specific ID and its status if ANY FOUND.
	 * 
	 * @param jobIds
	 * @return
	 */
	public List<ExecutionState>  selectJobs(String...jobIds);
	
	/**
	 * Submit child jobs using parent context
	 * 
	 * @param jobName
	 * @param parameters
	 * @return
	 */
	public String submitJob(String jobName, ExecutionState.Parameters parameters);
}
