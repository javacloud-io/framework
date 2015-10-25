package com.appe.task.impl;

import java.io.IOException;

import javax.inject.Inject;

import com.appe.AppeException;
import com.appe.task.internal.TaskExecutorManager;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Same thing as task manager + the EXTERNALIZE the TASK.
 * 
 * @author tobi
 *
 */
public abstract class ExternalTaskManagerImpl extends TaskExecutorManager {
	protected ObjectMapper objectMapper;
	@Inject
	public ExternalTaskManagerImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	/**
	 * Converting string task to T.
	 * @param type
	 * @param task
	 * @return
	 */
	protected <T> T unmarshalTask(Class<T> type, String task) {
		try {
			return	objectMapper.readValue(task, type);
		} catch(IOException ex) {
			throw AppeException.wrap(ex); 
		}
	}
	
	/**
	 * Converting task T to string.
	 * 
	 * @param type
	 * @param task
	 * @return
	 */
	protected <T> String marshalTask(Class<T> type, T task) {
		try {
			return	objectMapper.writeValueAsString(task);
		} catch(IOException ex) {
			throw AppeException.wrap(ex);
		}
	}
}
