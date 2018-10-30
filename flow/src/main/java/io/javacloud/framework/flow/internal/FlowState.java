package io.javacloud.framework.flow.internal;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FlowState {
	private String 	name;
	private long 	startedAt;
	private int  	retryCount;
	private String 	stackTrace;
	
	private String 	instanceId;
	private boolean failed;
	
	//INPUT/OUTPUT
	private Object  input;
	private Dictionary attributes;
	public FlowState() {
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(long startedAt) {
		this.startedAt = startedAt;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isFailed() {
		return failed;
	}
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	/**
	 * 
	 * @return
	 */
	public Object getInput() {
		return input;
	}
	public void setInput(Object input) {
		this.input = input;
	}

	/**
	 * 
	 * @return
	 */
	public Dictionary getAttributes() {
		return attributes;
	}
	public void setAttributes(Dictionary attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * The result of final state is RESULT or INPUT
	 * @return
	 */
	public <T> T output() {
		Object output = attributes.get(StateContext.ATTRIBUTE_RESULT);
		if(output == null) {
			output = input;
		}
		return Objects.cast(output);
	}
}
