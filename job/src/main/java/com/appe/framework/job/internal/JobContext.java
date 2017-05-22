package com.appe.framework.job.internal;

import com.appe.framework.job.ExecutionContext;
import com.appe.framework.job.ExecutionStatus;
/**
 * 
 * @author ho
 *
 */
public abstract class JobContext implements ExecutionContext {
	private final JobInfo job;
	public JobContext(JobInfo job) {
		this.job = job;
	}
	
	@Override
	public String getId() {
		return job.getId();
	}

	@Override
	public int getRetryCount() {
		return job.getRetryCount();
	}

	@Override
	public String getName() {
		return job.getName();
	}

	@Override
	public ExecutionStatus getStatus() {
		return job.getStatus();
	}

	@Override
	public Parameters getParameters() {
		return null;
	}

	@Override
	public Attributes getAttributes() {
		return null;
	}
	
	/**
	 * return internal JOB
	 * @return
	 */
	public JobInfo getJob() {
		return job;
	}
}
