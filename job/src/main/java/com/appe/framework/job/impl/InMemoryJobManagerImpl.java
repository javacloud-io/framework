package com.appe.framework.job.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.internal.JobInfo;
import com.appe.framework.job.internal.JobManager;
import com.appe.framework.job.internal.JobQueue;
import com.appe.framework.job.internal.JobSelector;
import com.appe.framework.job.internal.JobState;
import com.appe.framework.util.Objects;
/**
 * Simple In-Memory JOB management
 * 
 * @author ho
 *
 */
@Singleton
public class InMemoryJobManagerImpl extends JobManager {
	private final ConcurrentHashMap<String, JobInfo> jobStore = new ConcurrentHashMap<>(1024);
	private final JobQueue executorQueue= new BlockingJobQueue();
	private final JobQueue trackerQueue = new BlockingJobQueue();
	public InMemoryJobManagerImpl() {
	}
	
	/**
	 * 
	 */
	@Override
	public JobQueue selectJobQueue(JobState state) {
		if(state == JobState.WAITING) {
			return trackerQueue;
		}
		return executorQueue;
	}
	
	/**
	 * Find all JOBs with correct information
	 */
	@Override
	public Map<String, ExecutionStatus> selectJobs(JobSelector selector) {
		Map<String, ExecutionStatus> jobs = Objects.asMap();
		for(JobInfo job: jobStore.values()) {
			if(isApplicableJob(job, selector)) {
				jobs.put(job.getId(), job.getStatus());
			}
			
			//COLLECT ENOUGH JOBS
			if(selector.getLimit() > 0
					&& jobs.size() >= selector.getLimit()) {
				break;
			}
		}
		return jobs;
	}
	
	/**
	 * Job in store and working version are the same instance of exist.
	 */
	@Override
	public void syncJob(JobInfo job) {
		job.setUpdated(new java.util.Date());
		jobStore.put(job.getId(), job);
	}
	
	/**
	 * return true if JOB is applicable with selector
	 * 
	 * @param job
	 * @param selector
	 * @return
	 */
	static boolean isApplicableJob(JobInfo job, JobSelector selector) {
		if(selector.getParentId() != null 
				&& ! selector.getParentId().equals(job.getParentId())) {
			return false;
		}
		if(selector.getJobIds() != null
				&& ! selector.getJobIds().contains(job.getId())) {
			return false;
		}
		if(selector.getStates() != null
				&& ! selector.getStates().contains(job.getState())) {
			return false;
		}
		return true;
	}
}
