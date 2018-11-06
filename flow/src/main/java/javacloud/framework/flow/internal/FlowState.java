package javacloud.framework.flow.internal;

import java.util.Map;

import javacloud.framework.flow.StateContext;
import javacloud.framework.util.Objects;

/**
 * Persistent across state execution in a flow.
 * 
 * @author ho
 *
 */
public class FlowState {
	//ACTION STATE
	private String 	name;			//executing state
	private long 	startedAt;		//started in EPOC mills
	private int  	tryCount;		
	private String 	stackTrace;
	
	//INPUT/OUTPUT
	private Object  input;
	private Map<String, Object> attributes;
	
	//FLOW STATE
	private String 	flowId;
	private boolean failed;
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
	public int getTryCount() {
		return tryCount;
	}
	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
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
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getExecutionId() {
		return flowId;
	}
	public void setExecutionId(String flowId) {
		this.flowId = flowId;
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
