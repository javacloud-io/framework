package com.appe.framework.job.internal;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.appe.framework.job.execution.JobQueue;
import com.appe.framework.job.execution.JobTask;
import com.appe.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class DelayJobQueue implements JobQueue {
	private final AtomicLong SEQ = new AtomicLong(); 
	private DelayQueue<DelayedJob> delayQueue = new DelayQueue<DelayedJob>();
	/**
	 * 
	 */
	public DelayJobQueue() {
	}
	
	/**
	 * Poll and wait until time out in seconds specified.
	 */
	@Override
	public JobTask poll(int timeoutSeconds) throws InterruptedException {
		DelayedJob delayed = delayQueue.poll(timeoutSeconds, TimeUnit.SECONDS);
		return (delayed == null? null : delayed.job);
	}
	
	/**
	 * PUT TO THE QUEUE
	 */
	@Override
	public void offer(JobTask job) {
		int delaySeconds = 0;
		delayQueue.offer(new DelayedJob(job, delaySeconds, SEQ.incrementAndGet()));
	}
	
	/**
	 * DELAYED TASK QUEUE, USING TIME TO EXECUTE & SEQ number to determine order of execution.
	 * @author tobi
	 *
	 * @param <T>
	 */
	static class DelayedJob implements Delayed {
		final JobTask job;
		final long timer;
		final long seq;
		public DelayedJob(JobTask job, int delaySeconds, long seq) {
			this.job   = job;
			this.timer = System.currentTimeMillis() + delaySeconds * 1000L;
			this.seq  = seq;
		}
		
		//MAKE SURE COMPARE EXECUTION SEQ
		@Override
		public int compareTo(Delayed delayed) {
			DelayedJob tdelayed = (DelayedJob)delayed;
			int sign = Objects.signum(this.timer - tdelayed.timer);
			if(sign == 0) {
				sign = Objects.signum(this.seq - tdelayed.seq);
			}
			return sign;
		}
		
		//RETURN REMAINING EXECUTION TIME
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(timer - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}
	}
}
