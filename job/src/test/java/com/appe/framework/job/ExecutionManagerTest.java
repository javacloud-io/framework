package com.appe.framework.job;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.appe.framework.internal.GuiceTestCase;
import com.appe.framework.job.execution.JobScheduler;
import com.appe.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class ExecutionManagerTest extends GuiceTestCase {
	@Inject
	private ExecutionManager executionManager;
	@Inject
	private JobScheduler jobScheduler;
	
	private List<String> jobIds;
	private String canceledJobId;
	/**
	 * START EXECUTION
	 */
	@Before
	public void startExecutor() {
		jobIds = Objects.asList();
		jobScheduler.startWorkers(2);
	}
	
	/**
	 * 
	 */
	protected void awaitForCompletion() {
		//WAITING FOR ALL JOBS TO FINISH
		boolean completed = false;
		while(!completed) {
			completed = true;
			for(String jobId: jobIds) {
				ExecutionStatus status = executionManager.getJobStatus(jobId);
				if(!ExecutionStatus.isCompleted(status) && status != ExecutionStatus.CANCEL) {
					completed = false;
					break;
				}
			}
			if(!completed) {
				Objects.sleep(100, TimeUnit.MILLISECONDS);
			}
		}
		//ASSERT CANCELED JOB
		Assert.assertEquals(ExecutionStatus.CANCEL, executionManager.getJobStatus(canceledJobId));
	}
	
	/**
	 * SHUTDOWN THE EXECUTION
	 */
	@After
	public void shutdown() {
		awaitForCompletion();
		jobScheduler.shutdown(true);
	}
	
	/**
	 * Dummy test and gratefully shutdown
	 */
	@Test
	public void testHello() {
		for(int i = 0; i < 10; i ++) {
			String jobId = executionManager.submitJob("HelloAction", Objects.asDict("test", 123));
			jobIds.add(jobId);
		}
		
		//TEST CANCELING
		Collections.shuffle(jobIds);
		canceledJobId = jobIds.get(0);
		executionManager.cancelJob(canceledJobId);
	}
}
