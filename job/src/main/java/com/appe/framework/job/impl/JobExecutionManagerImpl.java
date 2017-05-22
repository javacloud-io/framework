package com.appe.framework.job.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.job.ExecutionContext.Parameters;
import com.appe.framework.job.ExecutionManager;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.internal.JobInfo;
import com.appe.framework.job.internal.JobState;
import com.appe.framework.job.internal.JobManager;
import com.appe.framework.job.internal.JobSelector;
import com.appe.framework.util.Objects;
/**
 * Simple job execution with 2 different QEUEUE:
 * 
 * 1. Execution QUEUE
 * 2. Waiting QUEUE
 * 
 * @author ho
 *
 */
@Singleton
public class JobExecutionManagerImpl implements ExecutionManager {
	private static final Logger logger = LoggerFactory.getLogger(JobExecutionManagerImpl.class);
	private ExecutorService executorService;
	
	private JobManager jobManager;
	@Inject
	public JobExecutionManagerImpl(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	/**
	 * Submit a job with NAME and parameters
	 */
	@Override
	public String submitJob(String jobName, Parameters parameters) {
		JobInfo job = new JobInfo(jobName);
		//TODO: add parameters
		return jobManager.submitJob(job);
	}
	
	/**
	 * return some jobs and its statuses
	 */
	@Override
	public Map<String, ExecutionStatus> selectJobs(String... jobIds) {
		JobSelector selector = new JobSelector();
		int limit = 10;
		if(Objects.isEmpty(jobIds)) {
			selector.setStates(Objects.asSet(JobState.RETRYING, JobState.RUNNING, JobState.WAITING));
		} else {
			selector.setJobIds(Objects.asSet(jobIds));
			limit = selector.getJobIds().size();
		}
		return jobManager.selectJobs(selector, limit);
	}

	/**
	 * Executor only started from the worker NODE!
	 * 
	 * @param numberOfWorkers
	 * @return
	 */
	@Override
	public boolean startExecutor(int numberOfWorkers) {
		//ALREADY STARTED => SHUTDOWN BEFORE RESTART
		if(executorService != null) {
			logger.warn("The service executor already started!");
			return false;
		}
		
		//START EXECUTION POOL
		logger.info("Start executor with {} workers and {} tracker", numberOfWorkers, 1);
		executorService = createExecutor(numberOfWorkers + 1);
		for(int i = 0; i < numberOfWorkers; i ++) {
			executorService.submit(new JobExecutor(jobManager));
		}
		executorService.submit(new JobTracker(jobManager));
		return true;
	}
	
	/**
	 * Make sure to correctly persist JOBs state prior toe shutdown the execution. Half running JOB might
	 * have unexpected behavior...
	 * 
	 * @param force
	 */
	@Override
	public boolean shutdown(boolean force) {
		//NOTHING TO SHUTDOWN FOE NOW
		if(executorService == null) {
			logger.warn("Execution service is already terminated!");
			return false;
		}
		
		//MAKE SURE TO gratefully shutdown
		logger.debug("Shutting down the execution service...");
		executorService.shutdownNow();
		
		//RESET THE EXECUTOR SERVICE
		executorService = null;
		logger.info("Execution service is gratefully terminated!");
		return true;
	}
	
	/**
	 * Keep spin off new worker threads
	 * 
	 * @param numberOfWorker
	 * @return
	 */
	protected ExecutorService createExecutor(int numberOfWorkers) {
		return	Executors.newFixedThreadPool(numberOfWorkers);
	}
}
