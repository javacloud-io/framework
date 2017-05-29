package com.appe.framework.job.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.job.execution.JobQueue;
import com.appe.framework.job.execution.JobScheduler;
import com.appe.framework.job.execution.JobTask;
import com.appe.framework.job.internal.BlockingJobQueue;
import com.appe.framework.job.internal.ReadyJobExecutor;
import com.appe.framework.job.internal.WaitingJobTracker;
import com.appe.framework.job.management.JobInfo;
import com.appe.framework.job.management.JobManager;
import com.appe.framework.job.management.JobState;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class LocalJobScheduler extends JobScheduler {
	private static final Logger logger = LoggerFactory.getLogger(LocalJobScheduler.class);
	
	protected final JobQueue executorQueue= new BlockingJobQueue();
	protected final JobQueue trackerQueue = new BlockingJobQueue();
	protected ExecutorService executorService;
	
	@Inject
	public LocalJobScheduler(JobManager jobManager) {
		super(jobManager);
	}
	
	/**
	 * Route JOB to correct QUEUE, default just assuming route JOB directly to worker QUEUE.
	 * 
	 * @param job
	 * @return
	 */
	@Override
	protected String scheduleJob(JobInfo job) {
		if(job.getState() == JobState.WAITING) {
			trackerQueue.offer(new JobTask(job));
		} else {
			executorQueue.offer(new JobTask(job));
		}
		return job.getId();
	}
	
	/**
	 * Starting enough worker to track waiting/ready TASK.
	 */
	@Override
	public boolean startWorkers(int numberOfWorkers) {
		//ALREADY STARTED => SHUTDOWN BEFORE RESTART
		if(executorService != null) {
			logger.warn("The service executor already started!");
			return false;
		}
		
		//START WORKERS POOL
		logger.info("Start executor with {} workers", numberOfWorkers);
		executorService = Executors.newFixedThreadPool(numberOfWorkers + 1);
		for(int i = 0; i < numberOfWorkers; i ++) {
			executorService.submit(new ReadyJobExecutor(this, executorQueue));
		}
		executorService.submit(new WaitingJobTracker(this, trackerQueue));
		return true;
	}
	
	/**
	 * Terminate the executor
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
		if(force) {
			executorService.shutdownNow();
		} else {
			executorService.shutdown();
		}
		
		//RESET THE EXECUTOR SERVICE
		executorService = null;
		logger.info("Execution service is gratefully terminated!");
		return true;
	}
}
