package javacloud.framework.flow.spi;

import javacloud.framework.flow.spec.FlowDefinition;

/**
 * 
 * @author ho
 *
 */
public interface FlowClient {
	/**
	 * Register a flow and return registration
	 * 
	 * @param flowName
	 * @param flowDefinition
	 * @return
	 */
	public FlowRegister registerFlow(String flowName, FlowDefinition flowDefinition);
	
	/**
	 * 
	 * @param flowName
	 * @param reason
	 * @return
	 */
	public FlowRegister deleteFlow(String flowName, String reason);
	
	/**
	 * 
	 * @param flowName
	 * @param input
	 * @return
	 */
	public <T> FlowExecution startExecution(String flowName, T input, String surrogateKey);
	
	/**
	 * return execution details or nothing is not found from history
	 * 
	 * @param executionId
	 * @return
	 */
	public FlowExecution	getExecution(String executionId);
	
	/**
	 * 
	 * @param executionId
	 * @param reason
	 * @return
	 */
	public FlowExecution cancelExecution(String executionId, String reason);
}
