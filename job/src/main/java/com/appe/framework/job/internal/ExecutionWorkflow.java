package com.appe.framework.job.internal;

import com.appe.framework.job.ExecutionListener;
import com.appe.framework.job.ExecutionContext;
import com.appe.framework.job.ExecutionStatus;
/**
 * Using the RETRY to implement a simple workflow which has 2 set of ACTIONS:
 * 
 * 1. Set of ACTIONS need to be execute
 * 2. Set of ACTIONS need to be execute as part of FAILURE.
 * 3. Execution of WORKFLOW will break at the FIRST FAIL execution
 * 
 * Assuming actions on the same workflow sharing very much the same set of PARAMETERs
 * @author ho
 *
 */
public abstract class ExecutionWorkflow implements ExecutionListener {
	protected ExecutionWorkflow() {
	}
	
	/**
	 * 1. Find next action, if NULL => DONE IN GOOD
	 * 2. During the execution, if 
	 */
	@Override
	public ExecutionStatus onExecute(ExecutionContext executionContext) {
		String nextAction = null;
		if(nextAction == null) {
			
		}
		return ExecutionStatus.WAIT;
	}
	
	/**
	 * Make sure to return FALSE if not seen completion YET.
	 */
	@Override
	public boolean onCompletion(ExecutionContext executionContext) {
		//IF WORKFLOW IS CANCEL -> NEED TO FIGURE OUT TO REVERT STUFF
		if(ExecutionStatus.CANCEL == executionContext.getStatus()) {
			return true;
		}
		
		//KEEP GOING TIL DONE
		Boolean completed = executionContext.getAttribute("completed");
		return (completed != null && completed.booleanValue());
	}
}
