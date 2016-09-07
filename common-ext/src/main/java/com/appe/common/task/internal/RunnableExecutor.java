package com.appe.task.internal;

import com.appe.task.TaskExecutor;
/**
 * Quickly run an executable task, just WRAP & DELEGATE.
 * 
 * @author ho
 *
 */
public class RunnableExecutor implements TaskExecutor<Runnable> {
	@Override
	public void execute(Runnable task) throws Exception {
		task.run();
	}
}
