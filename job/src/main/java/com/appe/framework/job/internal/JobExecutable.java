package com.appe.framework.job.internal;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A worker runs continuously to poll job from system and then execute them. Job might have priority...
 * 
 * @author ho
 *
 */
public abstract class JobExecutable implements Runnable {
	private static final Logger logger 	  = LoggerFactory.getLogger(JobExecutable.class);
	private static final int WAIT_TIMEOUT = (int)TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);	//DEFAULT 1 HOUR EACH?
	
	private final JobPoller jobPoller;
	protected JobExecutable(JobPoller jobPoller) {
		this.jobPoller = jobPoller;
	}
	
	/**
	 * POLL & EXECUTE TASK UNTIL INTERRUPTED. Jobs should be drained prior to shutdown the executor
	 */
	@Override
	public final void run() {
		logger.debug("Waiting for jobs with {} (s) polling timeout", WAIT_TIMEOUT);
		while(true) {
			JobInfo job = null;
			try {
				job =  jobPoller.poll(WAIT_TIMEOUT);
				if(job != null) {
					logger.debug("Executing job: {}", job);
					execute(job);
					logger.debug("Completed job: {}", job);
				}
			} catch(InterruptedException ex) {
				if(job == null || onInterrupted(job)) {
					break;
				}
			} catch(Throwable ex) {
				onUncaughtException(job, ex);
			}
		}
	}
	
	/**
	 * Execute the JOB and do what it need to trigger finishing
	 * 
	 * @param job
	 */
	protected abstract void execute(JobInfo job);
	
	/**
	 * return false if current running task can't be interrupted
	 * 
	 * @param job
	 * @return
	 */
	protected boolean onInterrupted(JobInfo job) {
		logger.warn("Worker was interrupted while executing job: {}", job);
		return true;
	}
	
	/**
	 * HANDLE ANY EXCEPTION TO RECONEVER THE SYSTEM... IN THEORY ALL OF THAT CAN BE DONE IN EXECUTOR LEVEL
	 * WHICH CAN CAPTURE LOGIC OF OF PROCESSIN/RETRY...
	 * 
	 * @param job
	 * @param ex
	 */
	protected void onUncaughtException(JobInfo job, Throwable ex) {
		logger.error("Worker caught exception while executing job: {}", job, ex);
	}
}
