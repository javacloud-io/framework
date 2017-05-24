package com.appe.framework.job.internal;

import java.util.List;

import com.appe.framework.job.ExecutionAction;
import com.appe.framework.job.ExecutionState;
import com.appe.framework.job.ext.JobContext;
import com.appe.framework.job.ext.JobInfo;
import com.appe.framework.job.ext.JobManager;
import com.appe.framework.job.ext.JobPoller;
import com.appe.framework.job.ext.JobState;
import com.appe.framework.job.ext.JobWorker;

/**
 * Integrate with JobManager to safely set the correct JOB STATE
 * 
 * @author ho
 *
 */
public abstract class JobExecutor extends JobWorker {
	protected JobManager jobManager;
	public JobExecutor(JobManager jobManager, JobPoller jobPoller) {
		super(jobPoller);
		this.jobManager = jobManager;
	}
	
	/**
	 * Make sure to set JOB STATE to FAIL with a correct stack trace...Job already take of the QUEUE and
	 * will no longer submit back cause DONT know what todo.
	 */
	@Override
	protected void onUncaughtException(JobInfo job, Throwable ex) {
		super.onUncaughtException(job, ex);
		
		//TRACK CORRECT STATE
		job.setState(JobState.BLOCKED);
		job.setStatus(ExecutionState.Status.FAIL);
		
		//TODO: collect stack trace
		jobManager.syncJob(job);
	}
	
	/**
	 * Make sure to re-submit JOB if it's
	 * 
	 * @param jobAction
	 * @param jobContext
	 */
	protected void notifyCompletion(ExecutionAction jobAction, JobContext jobContext) {
		JobInfo job = jobContext.getJob();
		
		//IF JOB IS COMPLETED => NOTHING ELSE NEED TO BE DONE
		if(jobAction.onCompletion(jobContext)) {
			job.setState(JobState.TERMINATED);
			jobManager.syncJob(job);
		} else {
			job.setState(JobState.RETRYING);
			job.setStatus(ExecutionState.Status.RETRY);
			job.setRetryCount(job.getRetryCount() + 1);
			
			//PUSH JOB BACK TO JOB QUEUE
			jobManager.submitJob(job);
		}
	}
	
	/**
	 * return the completion status based on the combination of all child job statuses:
	 * 
	 * - If there is a CHILD that is not finished executing, => NOT COMPLETION
	 * - If there is any WARNING/FAIL => WARNING/FAIL
	 * - Otherwise => SUCCESS
	 * 
	 * @param childJobs
	 * @return
	 */
	public static final ExecutionState resolveJobState(List<ExecutionState> childJobs) {
		ExecutionState finalState = null;
		for(ExecutionState state: childJobs) {
			//STILL IN WAITING STATE => PUSH BACK TO QUEUE
			if(!ExecutionState.Status.isCompleted(state.getStatus())) {
				finalState = state;
				break;
			}
			
			//COMPLETION STATUS WARNING/FAIL
			if(state.getStatus() == ExecutionState.Status.FAIL) {
				finalState = state;
			} else if(state.getStatus() == ExecutionState.Status.WARNING) {
				if(finalState == null || finalState.getStatus() != ExecutionState.Status.FAIL) {
					finalState = state;
				}
			}
		}
		return finalState;
	}
	
	/**
	 * return the final status if not => return SUCCESS
	 * 
	 * @param childJobs
	 * @return
	 */
	public static final ExecutionState.Status resolveJobStatus(List<ExecutionState> childJobs) {
		ExecutionState finalState = resolveJobState(childJobs);
		return (finalState == null? ExecutionState.Status.SUCCESS: finalState.getStatus());
	}
}
