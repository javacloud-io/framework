package com.appe.framework.job;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class HelloAction implements ExecutionListener {
	private static final Logger logger = LoggerFactory.getLogger(HelloAction.class);
	@Override
	public ExecutionStatus onExecute(ExecutionContext executionContext) {
		logger.info("<" + executionContext.getRetryCount() + "> Hello world!");
		
		executionContext.scheduleJob("HelloChildAction", null);
		
		Objects.sleep(2, TimeUnit.SECONDS);
		return ExecutionStatus.WAIT;
	}

	@Override
	public boolean onCompletion(ExecutionContext executionContext) {
		//JOB IS CANCEL => DOING NOTHING
		if(executionContext.getStatus() == ExecutionStatus.CANCEL) {
			return true;
		}
		
		//KEEP RE-TRY
		if(executionContext.getRetryCount() < 3) {
			return false;
		}
		logger.info("Bye!!!");
		return true;
	}
}
