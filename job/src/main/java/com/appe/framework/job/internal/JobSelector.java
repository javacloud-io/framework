package com.appe.framework.job.internal;

import java.util.Set;

import com.appe.framework.util.Objects;

/**
 * To be able to do complicated slice/dice the JOB and states. A job satisfy all of the conditions will be pick.
 * 
 * @author ho
 *
 */
public final class JobSelector {
	private String parentId;
	private Set<String> jobIds;
	private Set<JobState> states;
	private int limit;
	public JobSelector() {
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
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getJobIds() {
		return jobIds;
	}

	public void setJobIds(Set<String> jobIds) {
		this.jobIds = jobIds;
		if(!Objects.isEmpty(jobIds)) {
			this.limit = jobIds.size();
		}
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
	
	/**
	 * 
	 * @return
	 */
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
