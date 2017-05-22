package com.appe.framework.job.internal;

/**
 * Use to submit job back to processing queue
 * 
 * @author ho
 *
 */
public interface JobQueue extends JobPoller {
	/**
	 * Add job to the QUEUE
	 * 
	 * @param job
	 */
	public void offer(JobInfo job);
}
