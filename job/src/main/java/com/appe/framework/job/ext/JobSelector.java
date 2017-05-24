package com.appe.framework.job.ext;

import java.util.Set;

import com.appe.framework.util.Objects;

/**
 * To be able to do complicated slice/dice the JOB and states. A job satisfy all of the conditions will be pick.
 * 
 * @author ho
 *
 */
public final class JobSelector {
	private Set<String> jobIds;
	private String 		parentId;
	private Set<JobState> states;
	public JobSelector() {
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getJobIds() {
		return jobIds;
	}
	public void setJobIds(Set<String> jobIds) {
		this.jobIds = jobIds;
	}
	public JobSelector withJobIds(Set<String> jobIds) {
		this.jobIds = jobIds;
		return this;
	}
	public JobSelector withJobIds(String... jobIds) {
		this.jobIds = Objects.asSet(jobIds);
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public JobSelector withParentId(String parentId) {
		this.parentId = parentId;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<JobState> getStates() {
		return states;
	}
	public void setStates(Set<JobState> states) {
		this.states = states;
	}
	public JobSelector withStates(Set<JobState> states) {
		this.states = states;
		return this;
	}
	public JobSelector withStates(JobState... states) {
		return withStates(Objects.asSet(states));
	}
}
