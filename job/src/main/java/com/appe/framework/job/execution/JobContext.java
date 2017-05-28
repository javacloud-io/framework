package com.appe.framework.job.execution;

import java.util.Enumeration;

import com.appe.framework.job.ExecutionContext;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.management.JobInfo;
/**
 * Run time execution
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
	public <T> T getParameter(String name) {
		return job.getParameters().get(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return job.getParameters().keyNames();
	}

	@Override
	public <T> T getAttribute(String name) {
		return job.getAttributes().get(name);
	}

	@Override
	public <T> void setAttribute(String name, T value) {
		job.getAttributes().set(name, value);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return job.getAttributes().keyNames();
	}

	/**
	 * return internal JOB
	 * @return
	 */
	public final JobInfo getJob() {
		return job;
	}
	
	@Override
	public String toString() {
		return job.toString();
	}
}
