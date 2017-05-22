package com.appe.framework.job.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.job.ExecutionAction;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.internal.JobContext;
import com.appe.framework.job.internal.JobInfo;
import com.appe.framework.job.internal.JobManager;
import com.appe.framework.job.internal.JobState;

/**
 * Basic logic for WAITING job tracking, the process starting by pooling JOB from WAITING queue then cross check
 * to see if all its children terminated or not. Then combine STATUS for parent JOB.
 * 
 * @author ho
 *
 */
public class JobTracker extends JobWorker {
	private static final Logger logger 	  = LoggerFactory.getLogger(JobTracker.class);
	public JobTracker(JobManager jobManager) {
		super(jobManager, jobManager.selectJobQueue(JobState.WAITING));
	}
	
	/**
	 * Combine all child jobs status to decide the PARENTs. JOB already in WAITING STATE
	 */
	@Override
	protected void execute(JobInfo job) {
		JobContext jobContext 		= jobManager.createJobContext(job);
		Map<String, ExecutionStatus> childJobs = jobContext.selectJobs();
		
		//RESOLVE FINAL RESULT
		ExecutionStatus finalStatus = resolveStatus(childJobs);
		logger.debug("Status: [{}], waiting job: {}", finalStatus, job);
		
		job.setStatus(finalStatus);
		if(!ExecutionStatus.isCompleted(finalStatus)) {
			job.setState(JobState.WAITING);
			
			//PUSH BACK TO WAITING QUEUE
			jobManager.submitJob(job);
		} else {
			ExecutionAction  jobAction = jobManager.resolveJobExecution(job);
			notifyCompletion(jobAction, jobContext);
		}
	}
}
