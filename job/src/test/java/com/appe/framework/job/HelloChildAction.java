package com.appe.framework.job;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.util.Objects;

/**
 * Test with child job
 * 
 * @author ho
 *
 */
public class HelloChildAction implements ExecutionListener {
	private static final Logger logger = LoggerFactory.getLogger(HelloChildAction.class);
	
	@Override
	public ExecutionStatus onExecute(ExecutionContext executionContext) {
		logger.info("Hello parents !");
		Objects.sleep(1, TimeUnit.SECONDS);
		return ExecutionStatus.SUCCESS;
	}

	@Override
	public boolean onCompletion(ExecutionContext executionContext) {
		logger.info("Bye parents!!!");
		return true;
	}
}
