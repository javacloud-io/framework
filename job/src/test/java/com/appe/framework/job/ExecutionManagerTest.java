package com.appe.framework.job;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.appe.framework.internal.GuiceTestCase;
import com.appe.framework.job.ext.JobParameters;
import com.appe.framework.job.internal.JobExecutor;
import com.appe.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class ExecutionManagerTest extends GuiceTestCase {
	@Inject
	private ExecutionManager executionManager;
	private Set<String> jobIds;
	/**
	 * START EXECUTION
	 */
	@Before
	public void startExecutor() {
		jobIds = Objects.asSet();
		executionManager.startExecutor(2);
	}
	
	/**
	 * SHUTDOWN THE EXECUTION
	 */
	@After
	public void shutdown() {
		//WAITING FOR ALL JOBS TO FINISHES
		while(true) {
			Map<String, ExecutionStatus> jobs = executionManager.selectJobs(jobIds.toArray(new String[0]));
			if(ExecutionStatus.isCompleted(JobExecutor.resolveStatus(jobs))) {
				break;
			}
			
			//TAKE A NAP
			Objects.sleep(1, TimeUnit.SECONDS);
		}
		executionManager.shutdown(true);
	}
	
	/**
	 * Dummy test and gratefully shutdown
	 */
	@Test
	public void testHello() {
		for(int i = 0; i < 10; i ++) {
			String jobId = executionManager.submitJob("HelloAction", JobParameters.build("test", 123));
			jobIds.add(jobId);
		}
	}
}
