package com.appe.framework.job.execution;

import java.util.Date;

import com.appe.framework.job.management.JobInfo;
import com.appe.framework.util.Identifiable;
/**
 * A small set of JobInfo to carry around the QUEUE, with JOB id and some extra information for debugging purpose
 * 
 * @author ho
 *
 */
public class JobTask extends Identifiable<String> {
	private Date timestamp;
	public JobTask() {
	}
	
	public JobTask(JobInfo job) {
		setId(job.getId());
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
