package io.javacloud.framework.flow;
/**
 * 
 * @author ho
 *
 */
public interface StateFlow {
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
