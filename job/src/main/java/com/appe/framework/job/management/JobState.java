package com.appe.framework.job.management;
/**
 * 1. A job just submit will has CREATED state
 * 2. When job get pickup by worker it state will turn to READY
 * 3. Then turn to RUNNING
 * 4. Finish till the end will have TERMINATED otherwise BLOCKED
 * 
 * A BLOCKED job can re-submit and RUN with same jobId
 * @author ho
 *
 */
public enum JobState {
	CREATED,
	RETRYING,
	CANCELING,
	
	READY,
	RUNNING,
	WAITING,
	
	CANCELED,
	FAILED,
	TERMINATED
}
