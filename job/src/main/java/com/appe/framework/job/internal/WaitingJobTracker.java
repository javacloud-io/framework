package com.appe.framework.job.internal;


import com.appe.framework.job.ExecutionAction;
import com.appe.framework.job.ExecutionState;
import com.appe.framework.job.ext.JobContext;
import com.appe.framework.job.ext.JobInfo;
import com.appe.framework.job.ext.JobManager;
import com.appe.framework.job.ext.JobState;

/**
 * Basic logic for WAITING job tracking, the process starting by pooling JOB from WAITING queue then cross check
 * to see if all its children terminated or not. Then combine STATUS for parent JOB.
 * 
 * @author ho
 *
 */
public class WaitingJobTracker extends JobExecutor {
	public WaitingJobTracker(JobManager jobManager) {
		super(jobManager, jobManager.selectJobQueue(JobState.WAITING));
	}
	
	/**
	 * Combine all child jobs status to decide the PARENTs. JOB already in WAITING STATE
	 */
	@Override
	protected void execute(JobInfo job) {
		JobContext jobContext 		= jobManager.createJobContext(job);
		ExecutionState finalState 	= resolveJobState(jobContext.selectJobs());
		
		//RESOLVE FINAL RESULT
		ExecutionState.Status finalStatus = (finalState == null? ExecutionState.Status.SUCCESS: finalState.getStatus());
		logger.debug("Tracking job: {} -> {}", job, finalStatus);
		
		if(!ExecutionState.Status.isCompleted(finalStatus)) {
			job.setState(JobState.WAITING);
			
			//PUSH BACK TO WAITING QUEUE
			jobManager.submitJob(job);
		} else {
			job.setStatus(finalStatus);
			ExecutionAction  jobAction = jobManager.resolveJobExecution(job);
			notifyCompletion(jobAction, jobContext);
		}
	}
}
