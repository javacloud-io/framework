package com.appe.framework.job;

import java.util.Map;

/**
 * 
 * @author ho
 *
 */
public interface ExecutionManager {
	/**
	 * Schedule a job for execution, any node in the cluster should be able to perform this operation.
	 * 
	 * @param jobName
	 * @param parameters
	 * @return
	 */
	public String scheduleJob(String jobName, Map<String, Object> parameters);
	
	/**
	 * return job execution status to see if it's completed
	 * 
	 * @param jobId
	 * @return
	 */
	public ExecutionStatus getJobStatus(String jobId);
	
	/**
	 * 
	 * @param jobId
	 * @return
	 */
	public boolean cancelJob(String jobId);
}
