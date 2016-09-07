package com.appe.task;
/**
 * Call to execute particular task, implementation should support concurrency.
 * 
 * @author tobi
 *
 * @param <T>
 */
public interface TaskExecutor<T> {
	/**
	 * Task should be execute with out exception at all, any exception should be graceful handled.
	 * 
	 * @param task
	 * @throws Exception
	 */
	public void execute(T task) throws Exception;
}
