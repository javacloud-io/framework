package com.appe.task;
/**
 * Tuple keeps producing task for executor, any implementation should support concurrency.
 * 
 * TODO:
 * 1. Should add API to see approximately how may message still in PENDING vs PROCESSING
 * 
 * @author tobi
 *
 * @param <T>
 */
public interface TaskPoller<T> {
	/**
	 * Maximum wait timeoutSeconds when polling, if nothing found will return NULL.
	 * 
	 * @param timeoutSeconds
	 * @return
	 * @throws InterruptedException
	 */
	public T poll(int timeoutSeconds) throws InterruptedException;
}
