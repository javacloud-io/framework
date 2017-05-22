package com.appe.framework.job.internal;
/**
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
