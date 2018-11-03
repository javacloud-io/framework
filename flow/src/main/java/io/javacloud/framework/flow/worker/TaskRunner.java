package io.javacloud.framework.flow.worker;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * 
 * @author ho
 *
 * @param <T>
 */
public abstract class TaskRunner<T> implements Runnable {
	private static final Logger logger = Logger.getLogger(TaskRunner.class.getName());
	
	private final TaskQueue<T> taskQueue;
	private final int timeoutSeconds;
	/**
	 * 
	 * @param coordinator
	 * @param timeoutSeconds
	 */
	public TaskRunner(TaskQueue<T> taskQueue, int timeoutSeconds) {
		this.taskQueue 		= taskQueue;
		this.timeoutSeconds = timeoutSeconds;
	}
	
	/**
	 * ENSURE TASK EXECUTION IS NOT LOSING TASK DUE TO UNEXPECTED EVENT
	 */
	@Override
	public void run() {
		try {
			T task = taskQueue.poll(timeoutSeconds, TimeUnit.SECONDS);
			//RUN ONLY IF AVAILABLE
			if(task != null) {
				logger.fine("Run task: " + task);
				run(task);
			}
		} catch(InterruptedException ex) {
			onInterrupted();
		}
	}
	
	/**
	 * Handle interrupted event
	 */
	protected void onInterrupted() {
		logger.fine("Worker being interrupted!");
	}
	
	/**
	 * 
	 * @param task
	 */
	protected abstract void run(T task);
}
