package com.appe.framework.job.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;


import com.appe.framework.job.ExecutionManager;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.execution.JobScheduler;
import com.appe.framework.job.management.JobInfo;
import com.appe.framework.util.Dictionary;

/**
 * Simple job execution with 2 different QEUEUE:
 * 
 * 1. Execution QUEUE
 * 2. Waiting QUEUE
 * 
 * @author ho
 *
 */
@Singleton
public class ExecutionManagerImpl implements ExecutionManager {
	private JobScheduler	jobScheduler;
	@Inject
	public ExecutionManagerImpl(JobScheduler	jobScheduler) {
		this.jobScheduler = jobScheduler;
	}
	
	/**
	 * 
	 */
	@Override
	public ExecutionStatus getJobStatus(String jobId) {
		JobInfo job = jobScheduler.getJobManager().findJob(jobId);
		return (job == null? null : job.getStatus());
	}
	
	/**
	 * 
	 */
	@Override
	public boolean cancelJob(String jobId) {
		return jobScheduler.cancelJob(jobId);
	}

	/**
	 * Submit a job with NAME and parameters
	 */
	@Override
	public String scheduleJob(String jobName, Map<String, Object> parameters) {
		JobInfo job = new JobInfo(jobName, new Dictionary(parameters));
		return jobScheduler.scheduleJob(job);
	}
}
