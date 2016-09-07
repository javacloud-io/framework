package com.appe.task.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.task.TaskExecutor;
import com.appe.task.TaskManager;
import com.appe.task.TaskPoller;
/**
 * Just to make sure if someone call for destruction.
 * 
 * @author tobi
 *
 */
public class LocalTaskManager implements TaskManager {
	private static final Logger logger = LoggerFactory.getLogger(LocalTaskManager.class);
	private ConcurrentMap<TaskPoller<?>, ExecutorService> executorServices = new ConcurrentHashMap<TaskPoller<?>, ExecutorService>();
	public LocalTaskManager() {
	}
	
	/**
	 * MAKE SURE TO START WORKER ONLY IF NOT STARTED. THIS CODE NOT 100% WORKING CORRECTLY!
	 */
	@Override
	public <T> boolean startExecutor(TaskPoller<T> poller, TaskExecutor<T> executor, int numberOfWorker) {
		//TODO: Should pass ThreadFactory so it make sense of what is the worker for.
		if(!executorServices.containsKey(poller)) {
			ExecutorService executorService = createExecutor(numberOfWorker);
			if(executorServices.putIfAbsent(poller, executorService) == null) {
				Runnable worker = createWorker(poller, executor);
				for(int i = 0; i < numberOfWorker; i ++) {
					executorService.submit(worker);
				}
				return true;
			}
		}
		
		//ALREADY STARTED!
		logger.warn("Executor :{} already started!", executor);
		return false;
	}
	
	/**
	 * SHUTDOWN ALL THE EXECUTORS & CLEAN UP.
	 */
	@Override
	public void shutdown() {
		for(ExecutorService executorService: executorServices.values()) {
			executorService.shutdownNow();
		}
		executorServices.clear();
	}
	
	/**
	 * Keep spin off new worker threads
	 * 
	 * @param numberOfWorker
	 * @return
	 */
	protected ExecutorService createExecutor(int numberOfWorker) {
		return	Executors.newFixedThreadPool(numberOfWorker);
	}
	
	/**
	 * WORKER WHICH IS RUNNING INFINITE LOOP
	 * @param poller
	 * @param executor
	 * @return
	 */
	protected <T> Runnable createWorker(TaskPoller<T> poller, TaskExecutor<T> executor) {
		return new TaskWorker<T>(poller, executor);
	}
}
