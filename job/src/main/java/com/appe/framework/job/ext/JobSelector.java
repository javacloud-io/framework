package com.appe.framework.job.ext;

import java.util.Set;

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
}
