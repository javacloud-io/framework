package com.appe.framework.job.ext;
/**
 * An abstract job poller to feed job to worker. To support job canceling we need:
 * 1. Signal the canceling
 * 2. Stop polling for new jobs
 * 3. Drain all jobs that poller already taken and push back to QUEUE
 * 
 * @author ho
 *
 */
public interface JobPoller {
	/**
	 * Maximum wait timeoutSeconds when polling, if nothing found will return NULL.
	 * 
	 * @param timeoutSeconds
	 * @return
	 * @throws InterruptedException
	 */
	public JobInfo poll(int timeoutSeconds) throws InterruptedException;
}
