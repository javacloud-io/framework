package com.appe.task.internal;

import java.util.concurrent.TimeUnit;

import com.appe.task.TaskExecutor;
import com.appe.util.Objects;
/**
 * Basic building block for all kind of re-try implement local/remote...In theory when something the retry(...)
 * will be invoked. Subclass will implement policy as well as what need to be done to re-try!
 * 
 * @author tobi
 *
 * @param <T>
 */
public abstract class RetryTaskExecutor<T> implements TaskExecutor<T> {
	private TaskExecutor<T> executor;
	public RetryTaskExecutor(TaskExecutor<T> executor) {
		this.executor = executor;
	}
	
	/**
	 * 1. Try to execute the task
	 * 2. If fail => check if can retry?
	 */
	@Override
	public void execute(T task) throws Exception {
		int attempt = 0;
		long startTime = System.currentTimeMillis();
		while(true) {
			try {
				executor.execute(task);
				break;
			} catch(InterruptedException ex) {
				//MAKE SURE TO ABORT
				Thread.currentThread().interrupt();
			} catch(Exception ex) {
				long duration = throttle(task, ex, ++ attempt, System.currentTimeMillis() - startTime);
				if(duration > 0) {
					Objects.sleep(duration, TimeUnit.MILLISECONDS);
				} else if(duration < 0) {
					break;
				}
			}
		}
	}
	
	/**
	 * return a positive number of milliseconds if still want to retry. Throws exception if like to abort.
	 * For remote retry, task should re-submit with re-try follow and return 0.
	 * 
	 * @param task
	 * @param ex
	 * @param attempt
	 * @param elapsed
	 * @return
	 * @throws Exception
	 */
	protected abstract long throttle(T task, Exception ex, int attempt, long elapsed)
			throws Exception;
}
