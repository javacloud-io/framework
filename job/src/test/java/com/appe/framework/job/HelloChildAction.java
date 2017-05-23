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
	public ExecutionState.Status onExecute(ExecutionContext executionContext) {
		System.out.println("Hello parents !");
		Objects.sleep(1, TimeUnit.SECONDS);
		return ExecutionState.Status.SUCCESS;
	}

	@Override
	public boolean onCompletion(ExecutionContext executionContext) {
		System.out.println("Bye parents!!!");
		return true;
	}
}
