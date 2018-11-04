package javacloud.framework.flow;

/**
 * https://states-language.net/spec.html
 * 
 * @author ho
 *
 */
public interface StateContext {
	public static final String ATTRIBUTE_RESULT = "StateResult";
	public static final String ATTRIBUTE_ERROR 	= "StateError";
	
	/**
	 * return execution ID;
	 * 
	 * @return
	 */
	public String getExecutionId();
	
	/**
	 * return initial/raw INPUT parameters: String, Number, Dictionary...
	 * 
	 * @return
	 */
	public <T> T getInput();
	
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
	 * In case of loop, how many time it's been running
	 * @return
	 */
	public int getTryCount();
}
