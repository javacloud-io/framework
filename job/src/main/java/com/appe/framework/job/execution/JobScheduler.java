package com.appe.framework.job.execution;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.AppeRegistry;
import com.appe.framework.job.ExecutionListener;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.management.JobInfo;
import com.appe.framework.job.management.JobManager;
import com.appe.framework.job.management.JobState;
import com.appe.framework.util.Dictionary;

/**
 * Scheduler have binding to DISTRIBUTED QUEUE to distribute job across NODE. It also has local queue for WORKER.
 * 
 * Scheduler -> poll job from distributed QUEUE -> distribute to local QUEUE
 * 			 -> submit job to distributed QUEUE
 * 
 * Worker 	 -> poll job from local QUEUE		-> execute IT
 * 
 * @author ho
 *
 */
public abstract class JobScheduler {
	private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);
	
	private JobManager jobManager;
	public JobScheduler(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	/**
	 * return internal job manager
	 * @return
	 */
	public JobManager getJobManager() {
		return jobManager;
	}
	
	/**
	 * Make sure sync up job with server prior to execution
	 * 
	 * @param job
	 * @return
	 */
	public JobContext createJobContext(JobTask task) {
		JobInfo job = jobManager.findJob(task.getId());
		if(job == null) {
			return null;
		}
		
		//CREATE DEFAULT JOB CONTEXT
		return new JobContext(job) {
			@Override
			public String submitJob(String jobName, Map<String, Object> parameters) {
				Dictionary jobParameters = new Dictionary();
				//inherit a copy parent parameters
				for(Enumeration<String> iter = getParameterNames(); iter.hasMoreElements(); ) {
					String name = iter.nextElement();
					if(parameters != null && parameters.containsKey(name)) {
						jobParameters.set(name, parameters.get(name));
					} else {
						jobParameters.set(name, getParameter(name));
					}
				}
				JobInfo childJob = new JobInfo(jobName, jobParameters);
				childJob.setParentId(getId());
				
				//submit child job
				return JobScheduler.this.submitJob(childJob);
			}
			
			//LOG TO LOGGER & ADD ACTIVITY
			@Override
			public void log(String message, Throwable t) {
				StringWriter writer = new StringWriter();
				//
				PrintWriter printer = new PrintWriter(writer);
				printer.println(message);
				printer.println(t.getMessage());
				
				t.printStackTrace(printer);
				printer.flush();
				
				log(writer.toString());
			}
			
			@Override
			public void log(String message) {
				logger.debug(message);
				jobManager.addActivity(getJob(), message);
			}
		};
	}
	
	/**
	 * By default job NAME will be use to lookup the execution with default class ExecutionListener, hower
	 * this can be override using JobParameters.
	 * 
	 * @param job
	 * @return
	 */
	public ExecutionListener resolveJobListener(JobInfo job) {
		ExecutionListener jobListener = AppeRegistry.get().getInstance(ExecutionListener.class, job.getName());
		return jobListener;
	}
	
	/**
	 * Schedule a JOB for execution...
	 * 
	 * @param job
	 * @return
	 */
	public String submitJob(JobInfo job) {
		//FILL IN MISSING INFO
		if(job.getState() == null) {
			job.setState(JobState.CREATED);
		}
		
		//SYNC METADATA TO STORE
		jobManager.syncJob(job);
		
		//ROUTE TO CORRECT QUEUE
		return scheduleJob(job);
	}
	
	/**
	 * Signal canceling for JOB. If job has multiple child job, canceling will apply for the NEXT processing
	 * return false if not able to cancel JOB.
	 * 
	 * @param jobId
	 * @return
	 */
	public boolean cancelJob(String jobId) {
		JobInfo job = jobManager.findJob(jobId);
		if(job == null || ExecutionStatus.isCompleted(job.getStatus())) {
			return false;
		}
		
		//SIGNAL JOB CANCELING
		job.setState(JobState.CANCELING);
		jobManager.syncJob(job);
		return true;
	}
	
	/**
	 * Route JOB to correct QUEUE, default just assuming route JOB directly to worker QUEUE.
	 * 
	 * @param job
	 * @return
	 */
	protected abstract String scheduleJob(JobInfo job);
	
	/**
	 * Start the schedule to handle WORKERS
	 * 
	 * @param numberOfWorkers
	 * @return
	 */
	public abstract boolean startWorkers(int numberOfWorkers);
	
	/**
	 * Gratefully shutdown the execution
	 * 
	 * @param force
	 * @return
	 */
	public abstract boolean shutdown(boolean force);
}
