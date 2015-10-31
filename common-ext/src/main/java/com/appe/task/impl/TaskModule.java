package com.appe.task.impl;

import com.appe.registry.internal.GuiceModule;
import com.appe.task.TaskManager;

/**
 * Register all the basic module
 * 
 * @author ho
 *
 */
public class TaskModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(TaskManager.class).to(LocalTaskManager.class);
	}
}
