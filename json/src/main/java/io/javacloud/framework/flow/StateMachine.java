package io.javacloud.framework.flow;
/**
 * 
 * @author ho
 *
 */
public interface StateMachine {
	/**
	 * return start STATE
	 * @return
	 */
	public String getStartAt();
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public StateFunction getState(String name);
}
