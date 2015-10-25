package com.appe.task.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.appe.task.TaskExecutor;
import com.appe.task.TaskManager;
import com.appe.task.TaskQueue;
import com.appe.task.TaskTuple;
/**
 * Just to make sure if someone call for destruction.
 * 
 * @author tobi
 *
 */
public abstract class TaskExecutorManager implements TaskManager {
	private ConcurrentMap<String, TaskQueue<?>> taskQueues = new ConcurrentHashMap<String, TaskQueue<?>>();
	private ConcurrentMap<TaskTuple<?>, ExecutorService> executorServices = new ConcurrentHashMap<TaskTuple<?>, ExecutorService>();
	public TaskExecutorManager() {
	}
	
	/**
	 * MAKE SURE TO CREATE QUEUE ONLY IF NOT DONE SO.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> TaskQueue<T> bindQueue(String name, Class<T> type) {
		if(!taskQueues.containsKey(name)) {
			taskQueues.putIfAbsent(name, createQueue(name, type));
		}
		return (TaskQueue<T>)taskQueues.get(name);
	}
	
	/**
	 * MAKE SURE TO START WORKER ONLY IF NOT STARTED. THIS CODE NOT 100% WORKING CORRECTLY!
	 */
	@Override
	public <T> boolean startExecutor(TaskTuple<T> tuple, TaskExecutor<T> executor, int numberOfWorker) {
		//TODO: Should pass ThreadFactory so it make sense of what is the worker for.
		if(!executorServices.containsKey(tuple)) {
			ExecutorService executorService = createExecutor(numberOfWorker);
			if(executorServices.putIfAbsent(tuple, executorService) == null) {
				Runnable poller = createPoller(tuple, executor);
				for(int i = 0; i < numberOfWorker; i ++) {
					executorService.submit(poller);
				}
				return true;
			}
		}
		
		//ALREADY STARTED!
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
	 * Create/initialize the QUEUE.
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	protected abstract <T> TaskQueue<T> createQueue(String name, Class<T> type);
	
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
	 * @param tuple
	 * @param executor
	 * @return
	 */
	protected <T> Runnable createPoller(TaskTuple<T> tuple, TaskExecutor<T> executor) {
		return new TaskPoller<T>(tuple, executor);
	}
}
