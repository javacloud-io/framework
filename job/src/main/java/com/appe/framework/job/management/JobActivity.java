package com.appe.framework.job.management;

import java.util.Date;

import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.util.Identifiable;

/**
 * Each time an activity is logged, a snapshot of jobId/retryCount and states will be capture for later references.
 * Activity can be label with KEY/VALUE for any other purpose.
 * 
 * JobInfo -> JobActivity
 * 
 * TODO: Need to support labeling the activity
 * @author ho
 *
 */
public class JobActivity extends Identifiable<String> {
	private String jobId;
	private int retryCount;
	
	//snapshot of status & state
	private ExecutionStatus status;
	private JobState state;
	
	private String message;		//message log
	private Date   timestamp;	//when did it happen
	public JobActivity() {
		
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	public ExecutionStatus getStatus() {
		return status;
	}
	public void setStatus(ExecutionStatus status) {
		this.status = status;
	}
	
	public JobState getState() {
		return state;
	}
	public void setState(JobState state) {
		this.state = state;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
