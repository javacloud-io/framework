package com.appe.framework.job;

import java.util.concurrent.TimeUnit;

import com.appe.framework.util.Objects;

/**
 * Test with child job
 * 
 * @author ho
 *
 */
public class HelloChildAction implements ExecutionAction {

	@Override
	public ExecutionStatus onExecute(ExecutionContext executionContext) {
		System.out.println("Hello parents !");
		Objects.sleep(1, TimeUnit.SECONDS);
		return ExecutionStatus.SUCCESS;
	}

	@Override
	public boolean onCompleted(ExecutionContext executionContext) {
		System.out.println("Bye parents!!!");
		return true;
	}
}
