package com.appe.framework.job.execution;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A worker runs continuously to poll job from system and then execute them. Job might have priority...
 * - Job is unit of work
 * - Job execution is unit of execution, assuming nobody else executing same JOB somewhere
 * 
 * @author ho
 *
 */
public abstract class JobWorker<Job> implements Runnable {
	protected static final Logger logger  = LoggerFactory.getLogger(JobWorker.class);
	private static final int WAIT_TIMEOUT = (int)TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);	//DEFAULT 1 HOUR EACH?
	
	protected JobWorker() {
	}
	
	/**
	 * POLL & EXECUTE TASK UNTIL INTERRUPTED. Jobs should be drained prior to shutdown the executor
	 */
	@Override
	public final void run() {
		logger.debug("Waiting for jobs with {} (s) polling timeout", WAIT_TIMEOUT);
		while(true) {
			Job job = null;
			try {
				job = poll(WAIT_TIMEOUT);
				if(job != null) {
					execute(job);
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
	 * return a ready to execute JOB
	 * 
	 * @param timeoutSeconds
	 * @throws InterruptedException
	 * @return
	 */
	protected abstract Job poll(int timeoutSeconds) throws InterruptedException;
	
	/**
	 * Execute the JOB and do what it need to trigger finishing
	 * 
	 * @param job
	 */
	protected abstract void execute(Job job);
	
	/**
	 * return false if current running task can't be interrupted
	 * 
	 * @param job
	 * @return
	 */
	protected boolean onInterrupted(Job job) {
		logger.warn("Worker was interrupted while executing job: {}", job);
		return true;
	}
	
	/**
	 * HANDLE ANY EXCEPTION TO RECONEVER THE SYSTEM... IN THEORY ALL OF THAT CAN BE DONE IN EXECUTOR LEVEL
	 * WHICH CAN CAPTURE LOGIC OF OF PROCESSIN/RETRY...
	 * 
	 * @param jobContext
	 * @param ex
	 */
	protected void onUncaughtException(Job job, Throwable ex) {
		if(job != null) {
			logger.error("Worker caught exception while executing job: {}", job, ex);
		} else {
			logger.error("Worker caught exception while waiting for job: {}", ex);
		}
	}
}
