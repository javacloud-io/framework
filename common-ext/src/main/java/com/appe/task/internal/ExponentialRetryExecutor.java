package com.appe.task.internal;

import com.appe.task.TaskExecutor;
/**
 * 
 * @author tobi
 *
 * @param <T>
 */
public class ExponentialRetryExecutor<T> extends RetryTaskExecutor<T> {
	private double 	backoffCoefficient;
	private long	sleepInterval;
	private int 	maxAttempt;
	private long 	maxSleep;
	private long	maxElapsed;
	/**
	 * 
	 * @param executor
	 * @param sleepInterval
	 * @param maxAttempt
	 * @param backoffCoefficient
	 * @param maxSleep
	 * @param maxElapsed
	 */
	public ExponentialRetryExecutor(TaskExecutor<T> executor, long sleepInterval, int maxAttempt,
			double backoffCoefficient, long maxSleep, long maxElapsed) {
		super(executor);
		this.sleepInterval = sleepInterval;
		this.backoffCoefficient = backoffCoefficient;
		this.maxAttempt = maxAttempt;
		this.maxSleep	= maxSleep;
		this.maxElapsed = maxElapsed;
	}
	
	/**
	 * Default maximum sleep duration to be 1 MINUE = 60 seconds.
	 * 
	 * @param executor
	 * @param sleepInterval
	 * @param maxAttempt
	 * @param backoffCoefficient
	 */
	public ExponentialRetryExecutor(TaskExecutor<T> executor, long sleepInterval, int maxAttempt,
			double backoffCoefficient) {
		this(executor, sleepInterval, maxAttempt, backoffCoefficient, 60000, Long.MAX_VALUE);
	}
	
	/**
	 * Just do fix interval using max attempt.
	 * @param executor
	 * @param sleepInterval
	 * @param maxAttempt
	 */
	public ExponentialRetryExecutor(TaskExecutor<T> executor, long sleepInterval, int maxAttempt) {
		this(executor, sleepInterval, maxAttempt, 1.0);
	}
	
	/**
	 * Trying with all exception, make sure to not reach the maximum attempt & elapsed time.
	 */
	@Override
	protected long throttle(T task, Exception ex, int attempt, long elapsed) throws Exception {
		//TOO MANY ATTEMPTED OR TOOK TOO LONG
		if(attempt >= maxAttempt || elapsed >= maxElapsed) {
			throw ex;
		}
		
		//CALCULATE THE BACK OFF
		double duration = Math.pow(backoffCoefficient, attempt - 1) * sleepInterval;
		return Math.min((long)duration, maxSleep);
	}
}
