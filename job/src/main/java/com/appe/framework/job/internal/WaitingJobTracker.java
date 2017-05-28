package com.appe.framework.job.internal;


import java.util.List;

import com.appe.framework.job.ExecutionListener;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.execution.JobContext;
import com.appe.framework.job.execution.JobPoller;
import com.appe.framework.job.execution.JobScheduler;
import com.appe.framework.job.management.JobInfo;
import com.appe.framework.job.management.JobFilter;
import com.appe.framework.job.management.JobState;

/**
 * Basic logic for WAITING job tracking, the process starting by pooling JOB from WAITING queue then cross check
 * to see if all its children terminated or not. Then combine STATUS for parent JOB.
 * 
 * @author ho
 *
 */
public class WaitingJobTracker extends JobExecutor {
	/**
	 * 
	 * @param jobScheduler
	 * @param jobPoller
	 */
	public WaitingJobTracker(JobScheduler jobScheduler, JobPoller jobPoller) {
		super(jobScheduler, jobPoller);
	}
	
	/**
	 * Combine all child jobs status to decide the PARENTs. JOB already in WAITING STATE
	 */
	@Override
	protected void execute(JobContext jobContext) {
		JobInfo job = jobContext.getJob();
		
		//RESOLVE FINAL RESULT
		ExecutionStatus finalStatus = resolveJobStatus(job);
		logger.debug("Tracking job: {} -> {}", job, finalStatus);
		
		if(!ExecutionStatus.isCompleted(finalStatus)) {
			job.setState(JobState.WAITING);
			
			//PUSH BACK TO WAITING QUEUE
			jobScheduler.submitJob(job);
		} else {
			job.setStatus(finalStatus);
			ExecutionListener  jobListener = jobScheduler.resolveJobListener(job);
			notifyCompletion(jobListener, jobContext);
		}
	}
	
	/**
	 * TODO: Job in WAITING tracker always has STATUS = WAIT, but for some reason such as: CANCEL its current STATUS
	 * might already different...
	 * 
	 * @param job
	 * @return
	 */
	protected ExecutionStatus resolveJobStatus(JobInfo job) {
		JobFilter filter = new JobFilter();
		filter.withParentId(job.getId())
				.withStates(JobState.CREATED, JobState.RETRYING, JobState.CANCELING, JobState.READY, JobState.RUNNING, JobState.WAITING);
		List<JobInfo> childJobs = jobScheduler.getJobManager().findJobs(filter, 1);
		
		//LOOK INTO CHILD JOBS
		ExecutionStatus finalStatus = ExecutionStatus.SUCCESS;
		for(JobInfo childJob: childJobs) {
			ExecutionStatus status = childJob.getStatus();
			
			//STILL IN WAITING STATE => PUSH BACK TO QUEUE
			if(!ExecutionStatus.isCompleted(status)) {
				finalStatus = status;
				break;
			}
			
			//COMPLETION STATUS WARNING/FAIL
			if(status == ExecutionStatus.FAIL) {
				finalStatus = status;
			} else if(status == ExecutionStatus.WARNING && finalStatus != ExecutionStatus.FAIL) {
				finalStatus = status;
			}
		}
		return finalStatus;
	}
}
