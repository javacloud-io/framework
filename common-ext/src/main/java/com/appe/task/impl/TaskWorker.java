package com.appe.task.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.task.TaskExecutor;
import com.appe.task.TaskPoller;

/**
 * Simple task WORKER just keep polling the JOB. It's dump enough to not even re-try any execution...
 * Caller should smart enough to wrapping around some of the RetryExecutor for that purpose.
 * 
 * @author tobi
 *
 * @param <T>
 */
public class TaskWorker<T> implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(TaskWorker.class);
	private static final int WAIT_TIMEOUT = (int)TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);	//DEFAULT 1 HOUR EACH?
	
	private TaskPoller<T> poller;
	private TaskExecutor<T> executor;
	/**
	 * Poller using tuple and an executor, keep polling & execute them one by one.
	 * @param poller
	 * @param executor
	 */
	public TaskWorker(TaskPoller<T> poller, TaskExecutor<T> executor) {
		this.poller   = poller;
		this.executor = executor;
	}
	
	/**
	 * 
	 * POLL & EXECUTE TASK UNTIL INTERRUPTED
	 */
	@Override
	public void run() {
		while(true) {
			T task = null;
			try {
				//POLL & EXECUTE ONLY IF HAVE TASK
				task =  poller.poll(WAIT_TIMEOUT);
				if(task != null) {
					executor.execute(task);
				}
			} catch(InterruptedException ex) {
				logger.warn("Poller for executor: {} is interrupted, reason: {}", executor, ex.getMessage(), ex.getCause());
				break;
			} catch(Throwable ex) {
				caught(task, ex);
			}
		}
	}
	
	/**
	 * HANDLE ANY EXCEPTION TO RECONEVER THE SYSTEM... IN THEORY ALL OF THAT CAN BE DONE IN EXECUTOR LEVEL
	 * WHICH CAN CAPTURE LOGIC OF OF PROCESSIN/RETRY...
	 * 
	 * @param task
	 * @param ex
	 */
	protected void caught(T task, Throwable ex) {
		logger.error("Executor: {} caught exception while processing task: {}", executor, task, ex);
	}
}
