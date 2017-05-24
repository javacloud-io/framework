package com.appe.framework.job.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import com.appe.framework.job.ext.JobInfo;
import com.appe.framework.job.ext.JobManager;
import com.appe.framework.job.ext.JobQueue;
import com.appe.framework.job.ext.JobSelector;
import com.appe.framework.job.ext.JobState;
import com.appe.framework.job.internal.BlockingJobQueue;
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
	public JobQueue bindJobQueue(JobState state) {
		if(state == JobState.WAITING) {
			return trackerQueue;
		}
		return executorQueue;
	}
	
	/**
	 * Find all JOBs with correct information
	 */
	@Override
	public List<JobInfo> selectJobs(JobSelector selector, int limit) {
		List<JobInfo> jobs = Objects.asList();
		
		if(!Objects.isEmpty(selector.getJobIds())) {
			for(String jobId: selector.getJobIds()) {
				JobInfo job = jobStore.get(jobId);
				if(job != null && isApplicableJob(job, selector)) {
					jobs.add(job);
				}
			}
		} else {
			for(JobInfo job: jobStore.values()) {
				if(isApplicableJob(job, selector)) {
					jobs.add(job);
				}
				
				//COLLECT ENOUGH JOBS
				if(limit > 0 && jobs.size() >= limit) {
					break;
				}
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
				&& !selector.getParentId().equals(job.getParentId())) {
			return false;
		}
		//ALREADY satisfied
		//if(selector.getJobIds() != null
		//		&& ! selector.getJobIds().contains(job.getId())) {
		//	return false;
		//}
		if(selector.getStates() != null
				&& !selector.getStates().contains(job.getState())) {
			return false;
		}
		return true;
	}
}
