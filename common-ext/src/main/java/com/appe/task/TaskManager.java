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
	 * Register a Queue name with element type, return the existing one if already created. Queue are unique by NAME but TYPE
	 * help message serialization.
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public <T> TaskQueue<T> bindQueue(String name, Class<T> type);
	
	/**
	 * Start an executor to burn down tasks, just number of worker is needed. return FALSE if an executor already
	 * started for this tuple.
	 * 
	 * @param tuple
	 * @param executor
	 * @param numberOfWorker
	 * @return
	 */
	public <T> boolean startExecutor(TaskTuple<T> tuple, TaskExecutor<T> executor, int numberOfWorker);
	
	/**
	 * 
	 */
	public void shutdown();
}
