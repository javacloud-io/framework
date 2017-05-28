package com.appe.framework.job.management;

import java.util.List;


/**
 * Providing basic jobs management
 * 
 * @author ho
 *
 */
public interface JobManager {
	/**
	 * Get a job from store using ID
	 * 
	 * @param jobId
	 * @return
	 */
	public JobInfo findJob(String jobId);
	
	/**
	 * Make sure to take best effort to sync job state back to store.
	 * 
	 * @param job
	 */
	public void syncJob(JobInfo job);
	
	/**
	 * Add an activity message with JOB
	 * 
	 * @param job
	 * @param message
	 * @return
	 */
	public void addActivity(JobInfo job, String message);
	
	/**
	 * return a map of JOBs and it status which satisfy the selector
	 * 
	 * @param filter
	 * @param limit
	 * 
	 * @return
	 */
	public List<JobInfo> findJobs(JobFilter filter, int limit);
}
