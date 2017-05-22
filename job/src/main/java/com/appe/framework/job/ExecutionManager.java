package com.appe.framework.job;

import java.util.Map;

import com.appe.framework.job.ExecutionContext.Parameters;

/**
 * Using AppeRegistry to lookup JOB by its NAME with instance of JobExecutable
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
	public String submitJob(String jobName, Parameters parameters);
	
	/**
	 * Select some jobs and its status. If no jobs is specify, system will return SOME JOBS that haven't completed PROCESSING YET.
	 * 
	 * @param jobIds
	 * @return
	 */
	public Map<String, ExecutionStatus>  selectJobs(String...jobIds);
	
	/**
	 * Only the worker nodes would need to do this. Assuming a worker node can process all tasks for now.
	 * To be more efficient, each set of workers should handle set of JOB. This will definitely need to support.
	 * 
	 * @param numberOfWorkers
	 * @return
	 */
	public boolean startExecutor(int numberOfWorkers);
	
	/**
	 * Make sure to shutdown correctly
	 * 
	 * @param force
	 * @return
	 */
	public boolean shutdown(boolean force);
}
