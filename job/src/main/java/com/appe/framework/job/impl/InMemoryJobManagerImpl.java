package com.appe.framework.job.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import com.appe.framework.job.management.JobInfo;
import com.appe.framework.job.management.JobManager;
import com.appe.framework.job.management.JobFilter;
import com.appe.framework.util.Codecs;
import com.appe.framework.util.Objects;
/**
 * Simple In-Memory JOB management
 * 
 * @author ho
 *
 */
@Singleton
public class InMemoryJobManagerImpl implements JobManager {
	private final ConcurrentHashMap<String, JobInfo> jobStore = new ConcurrentHashMap<>(1024);
	public InMemoryJobManagerImpl() {
	}
	
	/**
	 * 
	 */
	@Override
	public JobInfo findJob(String jobId) {
		return jobStore.get(jobId);
	}
	
	/**
	 * 
	 */
	@Override
	public void addActivity(JobInfo job, String message) {
	}
	
	/**
	 * Find all JOBs with correct information
	 */
	@Override
	public List<JobInfo> findJobs(JobFilter filter, int limit) {
		List<JobInfo> jobs = Objects.asList();
		
		if(!Objects.isEmpty(filter.getJobIds())) {
			for(String jobId: filter.getJobIds()) {
				JobInfo job = jobStore.get(jobId);
				if(job != null && isApplicableJob(job, filter)) {
					jobs.add(job);
				}
			}
		} else {
			for(JobInfo job: jobStore.values()) {
				if(isApplicableJob(job, filter)) {
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
		if(job.getId() == null) {
			job.setId(Codecs.randomID());
			job.setCreated(new java.util.Date());
		}
		job.setUpdated(new java.util.Date());
		jobStore.put(job.getId(), job);
	}
	
	/**
	 * return true if JOB is applicable with selector
	 * 
	 * @param job
	 * @param filter
	 * @return
	 */
	protected boolean isApplicableJob(JobInfo job, JobFilter filter) {
		if(filter.getParentId() != null 
				&& !filter.getParentId().equals(job.getParentId())) {
			return false;
		}
		//ALREADY satisfied
		//if(filter.getJobIds() != null
		//		&& ! filter.getJobIds().contains(job.getId())) {
		//	return false;
		//}
		if(filter.getStates() != null
				&& !filter.getStates().contains(job.getState())) {
			return false;
		}
		return true;
	}
}
