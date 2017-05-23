package com.appe.framework.job;

import java.util.concurrent.TimeUnit;

import com.appe.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class HelloAction implements ExecutionAction {

	@Override
	public ExecutionState.Status onExecute(ExecutionContext executionContext) {
		System.out.println("<" + executionContext.getRetryCount() + "> Hello world!");
		executionContext.submitJob("HelloChildAction", null);
		Objects.sleep(2, TimeUnit.SECONDS);
		return ExecutionState.Status.WAIT;
	}

	@Override
	public boolean onCompletion(ExecutionContext executionContext) {
		if(executionContext.getRetryCount() < 3) {
			return false;
		}
		System.out.println("Bye!!!");
		return true;
	}
}
