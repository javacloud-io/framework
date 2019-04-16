package javacloud.framework.flow.worker;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Poll available task from task Queue and execute if available. This work in conjunction with TaskPoller to avoid flooding the workers.
 * 
 * @author ho
 *
 * @param <T>
 */
public abstract class TaskRunner<T> implements Runnable {
	private static final Logger logger = Logger.getLogger(TaskRunner.class.getName());
	
	private final ReservationQueue<T> taskQueue;
	private final int timeoutSeconds;
	/**
	 * 
	 * @param coordinator
	 * @param timeoutSeconds
	 */
	public TaskRunner(ReservationQueue<T> taskQueue, int timeoutSeconds) {
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
				logger.log(Level.FINE, "Running task: {0}", task);
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
		logger.log(Level.FINE, "Worker being interrupted!");
	}
	
	/**
	 * 
	 * @param task
	 */
	protected abstract void run(T task);
}
