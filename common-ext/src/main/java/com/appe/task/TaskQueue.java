package com.appe.task;


/**
 * Queue of tasks which you can add in something and can get back something else.
 * 
 * @author tobi
 *
 * @param <T>
 */
public interface TaskQueue<T> extends TaskPoller<T> {
	/**
	 * 
	 * @param task
	 */
	public void offer(T task);
	
	/**
	 * 
	 * @param task
	 * @param delaySeconds
	 */
	public void offer(T task, int delaySeconds);
}
