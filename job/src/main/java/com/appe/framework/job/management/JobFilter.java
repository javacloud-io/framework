package com.appe.framework.job.management;

import java.util.Set;

import com.appe.framework.util.Objects;

/**
 * To be able to do complicated slice/dice the JOB and states. A job satisfy all of the conditions will be pick.
 * 
 * @author ho
 *
 */
public final class JobFilter {
	private Set<String> jobIds;
	private String 		parentId;
	private Set<JobState> states;
	public JobFilter() {
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
	public JobFilter withJobIds(Set<String> jobIds) {
		this.jobIds = jobIds;
		return this;
	}
	public JobFilter withJobIds(String... jobIds) {
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
	public JobFilter withParentId(String parentId) {
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
	public JobFilter withStates(Set<JobState> states) {
		this.states = states;
		return this;
	}
	public JobFilter withStates(JobState... states) {
		return withStates(Objects.asSet(states));
	}
}
