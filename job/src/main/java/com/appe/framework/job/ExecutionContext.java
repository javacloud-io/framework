package com.appe.framework.job;

import java.util.Enumeration;
import java.util.Map;

/**
 * Job execution will have context parameters/attributes with NAME/VALUE. A job can use context to submit child jobs
 * as well as doing some basic child operation by select them.
 * 
 * @author ho
 *
 */
public interface ExecutionContext {
	/**
	 * return UNIQUE execution ID if it still alive
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * return NAME of execution to lookup listener
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * return run id if being executed more than ONE. in case of RETRY/LOOP
	 * @return
	 */
	public int getRetryCount();
	
	
	/**
	 * return the current status of the JOB is finish otherwise NULL
	 */
	public ExecutionStatus getStatus();
	
	/**
	 * return current context parameters
	 * @return
	 */
	public <T> T getParameter(String name);
	
	/**
	 * 
	 * @return
	 */
	public Enumeration<String> getParameterNames();
	
	/**
	 * return current context attributes, change to attribute will be persisted across RUN
	 * @return
	 */
	public <T> T getAttribute(String name);
	
	/**
	 * 
	 * @param name
	 * @param value
	 */
	public <T> void setAttribute(String name, T value);
	
	/**
	 * 
	 * @return
	 */
	public Enumeration<String> getAttributeNames();
	
	/**
	 * Submit child jobs using parent context, parameters will be inherited by default
	 * 
	 * @param jobName
	 * @param parameters
	 * @return
	 */
	public String scheduleJob(String jobName, Map<String, Object> parameters);
	
	/**
	 * Log activity message
	 * @param message
	 */
	public void log(String message);
	
	/**
	 * 
	 * @param message
	 * @param t
	 */
	public void log(String message, Throwable t);
}
