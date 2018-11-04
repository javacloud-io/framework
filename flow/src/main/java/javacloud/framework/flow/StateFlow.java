package javacloud.framework.flow;
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
	public StateAction getState(String name);
}
