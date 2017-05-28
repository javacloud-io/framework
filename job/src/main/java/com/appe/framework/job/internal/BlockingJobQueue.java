package com.appe.framework.job.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.appe.framework.job.execution.JobQueue;
import com.appe.framework.job.execution.JobTask;

/**
 * Simple in memory blocking Queue, not respect the timeOu
 * 
 * @author ho
 *
 */
public class BlockingJobQueue implements JobQueue {
	private BlockingQueue<JobTask> queue;
	/**
	 * 
	 */
	public BlockingJobQueue() {
		this(new LinkedBlockingQueue<JobTask>());
	}
	
	/**
	 * 
	 * @param queue
	 */
	public BlockingJobQueue(BlockingQueue<JobTask> queue) {
		this.queue = queue;
	}
	
	/**
	 * 
	 */
	@Override
	public JobTask poll(int timeoutSeconds) throws InterruptedException {
		return queue.poll(timeoutSeconds, TimeUnit.SECONDS);
	}
	
	/**
	 * IGNORING DELAY!!!
	 */
	@Override
	public void offer(JobTask job) {
		queue.offer(job);
	}
}
