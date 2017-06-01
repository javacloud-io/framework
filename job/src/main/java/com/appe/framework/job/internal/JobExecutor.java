package com.appe.framework.job.internal;

import com.appe.framework.job.ExecutionListener;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.execution.JobContext;
import com.appe.framework.job.execution.JobTask;
import com.appe.framework.job.execution.JobPoller;
import com.appe.framework.job.execution.JobScheduler;
import com.appe.framework.job.execution.JobWorker;
import com.appe.framework.job.management.JobInfo;
import com.appe.framework.job.management.JobState;

/**
 * Integrate with JobManager to safely set the correct JOB STATE
 * 
 * @author ho
 *
 */
public abstract class JobExecutor extends JobWorker<JobContext> {
	protected JobScheduler 	jobScheduler;
	protected JobPoller 	jobPoller;
	/**
	 * 
	 * @param jobScheduler
	 * @param jobPoller
	 */
	public JobExecutor(JobScheduler jobScheduler, JobPoller jobPoller) {
		this.jobScheduler	= jobScheduler;
		this.jobPoller		= jobPoller;
	}
	
	/**
	 * Job poll from QUEUE might be out of date, need to sync up with 
	 */
	@Override
	protected JobContext poll(int timeoutSeconds) throws InterruptedException {
		JobTask task = jobPoller.poll(timeoutSeconds);
		JobContext jobContext = (task == null? null: jobScheduler.createJobContext(task));
		
		//CHECK IF JOB NEED TO ABORT
		return (jobContext != null? santityCheck(jobContext) : jobContext);
	}
	
	/**
	 * Check to see if JOB need to execute or NOT
	 * 
	 * @param jobContext
	 * @return
	 */
	protected JobContext santityCheck(JobContext jobContext) {
		JobInfo job = jobContext.getJob();
		//ALREADY COMPLETED
		if(job.getState() == JobState.TERMINATED || job.getState() == JobState.FAILED || job.getState() == JobState.CANCELED) {
			logger.warn("Check job : {} -> COMPLETED", job);
			return null;
		}
		
		//CANCELING JOB
		if(job.getState() == JobState.CANCELING) {
			logger.debug("Check job : {} -> CANCEL", job);
			
			job.setStatus(ExecutionStatus.CANCEL);
			notifyCompletion(jobContext, null);
			return null;
		}
		return jobContext;
	}
	
	/**
	 * Make sure to set JOB STATE to FAIL with a correct stack trace...Job already take of the QUEUE and
	 * will no longer submit back cause DONT know what todo.
	 */
	@Override
	protected void onUncaughtException(JobContext jobContext, Throwable ex) {
		super.onUncaughtException(jobContext, ex);
		
		//TRACK CORRECT STATE
		JobInfo job = jobContext.getJob();
		job.setState(JobState.FAILED);
		job.setStatus(ExecutionStatus.FAIL);
		
		//TODO: collect stack trace & add activity
		jobScheduler.getJobManager().syncJob(job);
	}
	
	/**
	 * Make sure to re-submit JOB if it's
	 * 
	 * @param jobContext
	 * @param jobListener
	 */
	protected void notifyCompletion(JobContext jobContext, ExecutionListener jobListener) {
		JobInfo job = jobContext.getJob();
		if(jobListener == null) {
			jobListener = jobScheduler.resolveJobListener(job);
		}
		
		//IF JOB IS COMPLETED => NOTHING ELSE NEED TO BE DONE
		if(jobListener.onCompletion(jobContext)) {
			boolean canceled = (job.getStatus() == ExecutionStatus.CANCEL); 
			job.setState(canceled? JobState.CANCELED : JobState.TERMINATED);
			jobScheduler.getJobManager().syncJob(job);
			
			//TODO: NOTIFY ALL CHILDS ABOUT CANCELING?
			if(canceled) {
				;
			}
			
			//IF PARENT IS WAITING => NOTIFY FOR TRACKING
			if(job.getParentId() != null) {
				JobInfo jobp = jobScheduler.getJobManager().findJob(job.getParentId());
				if(jobp.getState() == JobState.WAITING) {
					logger.debug("Notify parent job: {}", jobp);
					jobScheduler.scheduleJob(jobp);
				}
			}
		} else {
			job.setState(JobState.RETRYING);
			job.setStatus(ExecutionStatus.RETRY);
			job.setRetryCount(job.getRetryCount() + 1);
			
			//SCHEDULE JOB BACK TO RETRY
			jobScheduler.scheduleJob(job);
		}
	}
}
