package com.appe.framework.job.ext;

import java.util.List;

import com.appe.framework.AppeRegistry;
import com.appe.framework.job.ExecutionAction;
import com.appe.framework.job.ExecutionState;
import com.appe.framework.util.Codecs;
import com.appe.framework.util.Objects;

/**
 * At basic we able to do find jobs, schedule submition and syncJob state and other
 * 
 * TODO:
 * - Supports job canceling
 * - Supports job purging
 * - Ability to control JOB execution on certain set of cluster/namspace/role...
 * 
 * @author ho
 *
 */
public abstract class JobManager {
	/**
	 * return runtime job context for given JOB at time of execution
	 * 
	 * @param job
	 * @return
	 */
	public JobContext createJobContext(JobInfo job) {
		return new JobContext(job) {
			@Override
			public String submitJob(String jobName, ExecutionState.Parameters parameters) {
				//inherit a copy parent parameters
				JobInfo childJob = new JobInfo(jobName, new JobParameters(getParameters()).set(parameters));
				childJob.setParentId(getId());
				
				return JobManager.this.submitJob(childJob);
			}
			
			@Override
			public List<ExecutionState> selectJobs(String...jobIds) {
				JobSelector selector = new JobSelector()
					.withParentId(getId())
					.withJobIds(jobIds);
				return Objects.cast(JobManager.this.selectJobs(selector, selector.getJobIds().size()));
			}
		};
	}
	
	/**
	 * return the execution action for given JOB, by default just dynamically lookup from registry of JOB.
	 * All Execution has to register with Registry with unique name in the system
	 * 
	 * @return
	 */
	public ExecutionAction resolveJobExecution(JobInfo job) {
		ExecutionAction action = AppeRegistry.get().getInstance(ExecutionAction.class, job.getName());
		
		//TODO: CONDITIONAL DOING DECORACTION
		return action;
	}
	
	/**
	 * Schedule a job to be executed. Make sure to re-direct job to correct QUEUE
	 * 
	 * @param job
	 * @return
	 */
	public String submitJob(JobInfo job) {
		//FILL IN BASIC JOB INFO
		if(Objects.isEmpty(job.getId())) {
			job.setId(Codecs.randomID());
			job.setState(JobState.CREATED);
			job.setCreated(new java.util.Date());
		}
		
		//SYNC METADATA & SUBMIT TO CORRECT QUEUE
		syncJob(job);
		bindJobQueue(job.getState()).offer(job);
		
		return job.getId();
	}
	
	/**
	 * return the correct QUEUE for given job state. In theory, only WAITING has it own QUEUE but we are not
	 * limit to this selection strategy. As long as has WORKER on the queue, then submition is valid.
	 * 
	 * @param state
	 * @return
	 */
	public abstract JobQueue bindJobQueue(JobState state);
	
	/**
	 * return a map of JOBs and it status which satisfy the selector
	 * 
	 * @param selector
	 * @param limit
	 * 
	 * @return
	 */
	public abstract List<JobInfo> selectJobs(JobSelector selector, int limit);
	
	/**
	 * This have to be smart enough to know which changes can be update which is NOT. To correctly refresh
	 * the state of a job to make sure the execution always has the latest.
	 *  
	 * @param job
	 */
	public abstract void syncJob(JobInfo job);
}
