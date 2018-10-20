package io.javacloud.framework.flow;

import java.util.Map;

/**
 * 
 * @author ho
 *
 */
public interface StateContext {
	/**
	 * return execution ID;
	 * 
	 * @return
	 */
	public String getFlowId();
	
	/**
	 * return original INPUT parameters
	 * 
	 * @return
	 */
	public Map<String, Object> getParameters();
	
	/**
	 * Persistent attributes cross StepFunction
	 * 
	 * @return
	 */
	public Map<String, Object> getAttributes();
	
	/**
	 * return attribute value
	 * 
	 * @param name
	 * @return
	 */
	public <T> T getAttribute(String name);
	
	/**
	 * Update attribute value
	 * 
	 * @param name
	 * @param attribute
	 */
	public <T> void setAttribute(String name, T attribute);
	
	/**
	 * 
	 * @return
	 */
	public int getRetryCount();
}
