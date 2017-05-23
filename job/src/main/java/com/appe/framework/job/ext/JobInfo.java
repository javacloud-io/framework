package com.appe.framework.job.ext;

import java.util.Date;

import com.appe.framework.job.ExecutionAction;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.util.Identifiable;

/**
 * Persistent descriptor of JOB to be able to re-construct across machine...
 * 
 * @author ho
 *
 */
public class JobInfo extends Identifiable<String> {
	private String name;
	private int retryCount;
	private JobParameters parameters;
	private JobParameters attributes;
	
	private JobState state;
	private ExecutionStatus status;
	
	private String parentId;
	
	private Date created;
	private Date updated;
	public JobInfo() {
	}
	
	/**
	 * 
	 * @param name
	 * @param parameters
	 */
	public JobInfo(String name, ExecutionAction.Parameters parameters) {
		this.name = name;
		this.parameters = JobParameters.wrap(parameters);
		this.attributes = new JobParameters();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	/**
	 * 
	 * @return
	 */
	public JobParameters getParameters() {
		return parameters;
	}
	public void setParameters(JobParameters parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * 
	 * @return
	 */
	public JobParameters getAttributes() {
		return attributes;
	}
	public void setAttributes(JobParameters attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * 
	 * @return
	 */
	public JobState getState() {
		return state;
	}
	public void setState(JobState state) {
		this.state = state;
	}
	
	/**
	 * return execution status
	 * @return
	 */
	public ExecutionStatus getStatus() {
		return status;
	}
	public void setStatus(ExecutionStatus status) {
		this.status = status;
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
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * 
	 * @return
	 */
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	/**
	 * Not include the sensitive of parameters & attributes
	 */
	@Override
	public String toString() {
		return "{id=" + getId() + ", parentId=" + parentId + ", retryCount=" + retryCount + ", state=" + state + ", status=" + status + "}";
	}
}
