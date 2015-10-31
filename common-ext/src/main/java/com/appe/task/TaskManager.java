package com.appe.task;

/**
 * System always have task manager available, depends on role of the server it then:
 * 
 * 1. Register Queue to submit task
 * 2. Start worker executor to burn down task. If system need some sort of LOCAL QUEUE => There will be a simple
 * implementation of tuple which can plug-in for that purpose.
 * 
 * @author tobi
 *
 */
public interface TaskManager {
	/**
	 * Start an executor to burn down tasks, just number of worker is needed. return FALSE if an executor already
	 * started for this tuple.
	 * 
	 * @param poller
	 * @param executor
	 * @param numberOfWorker
	 * @return
	 */
	public <T> boolean startExecutor(TaskPoller<T> poller, TaskExecutor<T> executor, int numberOfWorker);
	
	/**
	 * Terminate and shutdown all workers... make sure to wait for long enough for worker to finish JOB
	 */
	public void shutdown();
}
