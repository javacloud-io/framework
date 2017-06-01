package com.appe.framework.job.internal;

import com.appe.framework.job.ExecutionListener;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.execution.JobContext;
import com.appe.framework.job.execution.JobPoller;
import com.appe.framework.job.execution.JobScheduler;
import com.appe.framework.job.management.JobInfo;
import com.appe.framework.job.management.JobState;
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
	/**
	 * CREATED or RETRYING should be using same processing QUEUE. Unless we using 2 set of workers
	 * to process them separately which is NOT FAIL for the JOB which already QUEUED.
	 * 
	 * @param jobScheduler
	 * @param jobPoller
	 */
	public ReadyJobExecutor(JobScheduler jobScheduler, JobPoller jobPoller) {
		super(jobScheduler, jobPoller);
	}
	
	/**
	 * 
	 */
	@Override
	protected void execute(JobContext jobContext) {
		JobInfo job = jobContext.getJob();
		ExecutionListener jobListener	= jobScheduler.resolveJobListener(job);
		
		//SWITCH JOB TO RUNNING STATE
		job.setState(JobState.RUNNING);
		jobScheduler.getJobManager().syncJob(job);
		
		//EXECUTE & UPDATE STATE
		ExecutionStatus status = jobListener.onExecute(jobContext);
		job.setStatus(status);
		
		if(ExecutionStatus.isCompleted(status)) {
			notifyCompletion(jobContext, jobListener);
		} else if(status == ExecutionStatus.WAIT) {
			job.setState(JobState.WAITING);
			
			//PUSH JOB BACK TO WAITING QUEUE?
			jobScheduler.scheduleJob(job);
		} else if(status == ExecutionStatus.RETRY) {
			job.setState(JobState.RETRYING);
			job.setRetryCount(job.getRetryCount() + 1);
			
			//PUSH JOB BACK TO JOB QUEUE
			jobScheduler.scheduleJob(job);
		}
	}
}
