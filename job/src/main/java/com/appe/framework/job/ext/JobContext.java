package com.appe.framework.job.ext;

import com.appe.framework.job.ExecutionAction;
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
	public ExecutionAction.Parameters getParameters() {
		return job.getParameters();
	}

	@Override
	public ExecutionAction.Attributes getAttributes() {
		return job.getAttributes();
	}
	
	/**
	 * return internal JOB
	 * @return
	 */
	public JobInfo getJob() {
		return job;
	}
}
