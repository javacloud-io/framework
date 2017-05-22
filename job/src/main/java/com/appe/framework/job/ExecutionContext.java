package com.appe.framework.job;

import java.util.Map;

/**
 * Job execution will have context parameters/attributes with NAME/VALUE
 * 
 * @author ho
 *
 */
public interface ExecutionContext {
	/**
	 * parameters to feed into JOB
	 */
	public interface Parameters {
		/**
		 * 
		 * @param name
		 * @return
		 */
		public <T> T get(String name);
	}
	
	/**
	 * attributes to persist state, can passing down to generation/children
	 */
	public interface Attributes extends Parameters {
		/**
		 * 
		 * @param name
		 * @param value
		 */
		public <T> void set(String name, T value);
	}
	
	/**
	 * return UNIQUE execution ID if it still alive
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * return NAME of execution action
	 * @return
	 */
	public String getName();
	
	/**
	 * return the current status of the JOB is finish otherwise NULL
	 */
	public ExecutionStatus getStatus();
	
	/**
	 * return run id if being executed more than ONE. in case of RETRY/LOOP
	 * @return
	 */
	public int getRetryCount();
	
	/**
	 * return current context parameters
	 * @return
	 */
	public Parameters getParameters();
	
	/**
	 * return the current context attributes
	 * 
	 * @return
	 */
	public Attributes getAttributes();
	
	/**
	 * return all children jobs or jobs with specific ID and its status if ANY FOUND.
	 * 
	 * @param jobIds
	 * @return
	 */
	public Map<String, ExecutionStatus>  selectJobs(String...jobIds);
	
	/**
	 * Submit child jobs using parent context
	 * 
	 * @param jobName
	 * @param parameters
	 * @return
	 */
	public String submitJob(String jobName, Parameters parameters);
}
