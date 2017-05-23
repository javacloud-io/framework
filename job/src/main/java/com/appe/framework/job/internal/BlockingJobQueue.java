package com.appe.framework.job.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.appe.framework.job.ext.JobInfo;
import com.appe.framework.job.ext.JobQueue;

/**
 * Simple in memory blocking Queue, not respect the timeOu
 * 
 * @author ho
 *
 */
public class BlockingJobQueue implements JobQueue {
	private BlockingQueue<JobInfo> queue;
	public BlockingJobQueue() {
		this(new LinkedBlockingQueue<JobInfo>());
	}
	public BlockingJobQueue(BlockingQueue<JobInfo> queue) {
		this.queue = queue;
	}
	
	/**
	 * 
	 */
	@Override
	public JobInfo poll(int timeoutSeconds) throws InterruptedException {
		return queue.poll(timeoutSeconds, TimeUnit.SECONDS);
	}
	
	/**
	 * IGNORING DELAY!!!
	 */
	@Override
	public void offer(JobInfo job) {
		queue.offer(job);
	}
}
