package com.appe.framework.job.internal;

import com.appe.framework.job.ExecutionAction;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.ext.JobContext;
import com.appe.framework.job.ext.JobInfo;
import com.appe.framework.job.ext.JobManager;
import com.appe.framework.job.ext.JobPoller;
import com.appe.framework.job.ext.JobState;
/**
 * Make sure execution jobQueue has to be smart enough to redirect WAITING jobs to waiting queue.
 * 
 * NOTES: in case of a system shutdown, some of jobs might losing current execution states.
 * It always re-sum with the last KNOW STATES.
 * 
 * @author ho
 *
 */
public class ReadyJobExecutor extends JobExecutor {
	public ReadyJobExecutor(JobManager jobManager, JobPoller jobPoller) {
		super(jobManager, jobPoller);
	}
	
	/**
	 * CREATED or RETRYING should be using same processing QUEUE. Unless we using 2 set of workers
	 * to process them separately which is NOT FAIL for the JOB which already QUEUED.
	 * 
	 * @param jobManager
	 */
	public ReadyJobExecutor(JobManager jobManager) {
		this(jobManager, jobManager.selectJobQueue(JobState.READY));
	}
	
	/**
	 * 
	 */
	@Override
	protected void execute(JobInfo job) {
		JobContext jobContext 		= jobManager.createJobContext(job);
		ExecutionAction jobAction 	= jobManager.resolveJobExecution(job);
		
		//SWITCH JOB TO RUNNING STATE
		job.setState(JobState.RUNNING);
		jobManager.syncJob(job);
		ExecutionStatus status = jobAction.onExecute(jobContext);
		
		job.setStatus(status);
		if(ExecutionStatus.isCompleted(status)) {
			notifyCompletion(jobAction, jobContext);
		} else if(status == ExecutionStatus.WAIT) {
			job.setState(JobState.WAITING);
			
			//PUSH JOB BACK TO WAITING QUEUE
			jobManager.submitJob(job);
		} else if(status == ExecutionStatus.RETRY) {
			job.setState(JobState.RETRYING);
			job.setRetryCount(job.getRetryCount() + 1);
			
			//PUSH JOB BACK TO JOB QUEUE
			jobManager.submitJob(job);
		}
	}
}
