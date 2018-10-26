package io.javacloud.framework.flow;

/**
 * https://states-language.net/spec.html
 * 
 * @author ho
 *
 */
public interface StateContext {
	public static final String ATTRIBUTE_RESULT 	= "StateResult";
	public static final String ATTRIBUTE_ERROR 		= "StateError";
	
	//BUILT-IN ERROR
	public static final String ERROR_ALL 	   		= "States.ALL";
	public static final String ERROR_TIMEOUT 		= "States.Timeout";
	public static final String ERROR_NOT_FOUND 		= "States.NotFound";
	public static final String ERROR_NOT_RETRYABLE 	= "States.NotRetryable";
	public static final String ERROR_JSON_CONVERTER	= "States.JsonConverter";
		
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
