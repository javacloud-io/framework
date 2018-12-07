package javacloud.framework.flow;
/**
 * Workflow describe as state machine with start STATE and transition to next...
 * 
 * @author ho
 *
 */
public interface StateFlow {
	/**
	 * return start STATE
	 * 
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
