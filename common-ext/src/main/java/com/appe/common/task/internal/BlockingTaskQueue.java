package com.appe.task.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.appe.task.TaskQueue;
/**
 * 
 * @author tobi
 *
 * @param <T>
 */
public class BlockingTaskQueue<T> implements TaskQueue<T> {
	private BlockingQueue<T> queue;
	public BlockingTaskQueue() {
		this(new LinkedBlockingQueue<T>());
	}
	public BlockingTaskQueue(BlockingQueue<T> queue) {
		this.queue = queue;
	}
	
	/**
	 * 
	 */
	@Override
	public T poll(int timeoutSeconds) throws InterruptedException {
		return queue.poll(timeoutSeconds, TimeUnit.SECONDS);
	}
	
	/**
	 * 
	 */
	@Override
	public void offer(T task) {
		queue.offer(task);
	}
	
	/**
	 * IGNORING DELAY!!!
	 */
	@Override
	public void offer(T task, int delaySeconds) {
		offer(task);
	}
}
