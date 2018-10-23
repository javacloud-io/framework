package io.javacloud.framework.flow;

/**
 * https://states-language.net/spec.html
 * 
 * @author ho
 *
 */
public interface StateContext {
	public static final String RESULT_ATTRIBUTE = "State.Result";
	
	/**
	 * return execution ID;
	 * 
	 * @return
	 */
	public String getFlowId();
	
	/**
	 * return initial/raw INPUT parameters: String, Number, Dictionary...
	 * 
	 * @return
	 */
	public <T> T getParameters();
	
	/**
	 * return internal state attribute value from setAttribute()
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
