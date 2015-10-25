package com.appe.task.internal;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.appe.task.TaskQueue;
import com.appe.util.Objects;
/**
 * Using the delay QUEUE to implement TaskQueue. I don't think it as fast as BlockingQueue.
 * 
 * @author tobi
 *
 * @param <T>
 */
public class DelayTaskQueue<T> implements TaskQueue<T> {
	private final AtomicLong SEQ = new AtomicLong(); 
	private DelayQueue<DelayedTask<T>> delayQueue = new DelayQueue<DelayedTask<T>>();
	/**
	 * 
	 */
	public DelayTaskQueue() {
	}
	
	/**
	 * Poll and wait until time out in seconds specified.
	 */
	@Override
	public T poll(int timeoutSeconds) throws InterruptedException {
		DelayedTask<T> delayed = delayQueue.poll(timeoutSeconds, TimeUnit.SECONDS);
		return (delayed == null? null : delayed.task());
	}
	
	/**
	 * ADD TASK WITHOUT DELAYS
	 */
	@Override
	public void offer(T task) {
		offer(task, 0);
	}
	
	/**
	 * PUT TO THE QUEUE
	 */
	@Override
	public void offer(T task, int delaySeconds) {
		delayQueue.offer(new DelayedTask<T>(task, delaySeconds, SEQ.incrementAndGet()));
	}
	
	/**
	 * DELAYED TASK QUEUE, USING TIME TO EXECUTE & SEQ number to determine order of execution.
	 * @author tobi
	 *
	 * @param <T>
	 */
	static class DelayedTask<T> implements Delayed {
		private T task;
		private long timer;
		private long seq;
		public DelayedTask(T task, int delaySeconds, long seq) {
			this.task = task;
			this.timer = System.currentTimeMillis() + delaySeconds * 1000L;
			this.seq  = seq;
		}
		
		/**
		 * return current task.
		 * @return
		 */
		public T task() {
			return task;
		}
		
		//1. MAKE SURE COMPARE EXECUTION
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Delayed delayed) {
			DelayedTask<T> tdelayed = (DelayedTask<T>)delayed;
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
