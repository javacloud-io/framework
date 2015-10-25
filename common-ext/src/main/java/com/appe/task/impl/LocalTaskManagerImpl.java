package com.appe.task.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.appe.task.TaskQueue;
import com.appe.task.internal.LocalTaskQueue;
import com.appe.task.internal.TaskExecutorManager;
/**
 * Local task processing delegate to java executor with single thread pool
 * 
 * @author tobi
 *
 */
public class LocalTaskManagerImpl extends TaskExecutorManager {
	private ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
	public LocalTaskManagerImpl() {
	}
	
	/**
	 * 
	 */
	@Override
	protected <T> TaskQueue<T> createQueue(String name, Class<T> type) {
		return new DelayedQueue<T>(name);
	}
	
	/**
	 * MAKE SURE TO SHUTDOWN THE SCHEDULED.
	 */
	@Override
	public void shutdown() {
		try {
			scheduledService.shutdownNow();
		} finally {
			super.shutdown();
		}
	}

	//SIMPLE TASK QUEUE WHICH RESPECT DELAY
	class DelayedQueue<T> extends LocalTaskQueue<T>{
		private String name;
		public DelayedQueue(String name) {
			super();
			this.name = name;
		}
		
		@Override
		public void offer(T task, int delaySeconds) {
			if(delaySeconds > 0) {
				scheduledService.schedule(new DelayedTask<T>(this, task), delaySeconds, TimeUnit.SECONDS);
			} else {
				super.offer(task);
			}
		}
		
		//RESONABLE STRING
		@Override
		public String toString() {
			return "Queue: " + name;
		}
	}
	
	//WRAP THE DELAY TASK
	class DelayedTask<T> implements Runnable {
		private TaskQueue<T> queue;
		private T task;
		public DelayedTask(TaskQueue<T> queue, T task) {
			this.queue = queue;
			this.task  = task;
		}
		
		@Override
		public void run() {
			queue.offer(task);
		}
	}
}
