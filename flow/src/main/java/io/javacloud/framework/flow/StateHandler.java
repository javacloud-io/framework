package io.javacloud.framework.flow;

/**
 * 
 * @author ho
 *
 */
public interface StateHandler<T> {
	//BUILT-IN ERRORS
	public static final String ERROR_ALL 	   		= "States.ALL";
	public static final String ERROR_TIMEOUT 		= "States.Timeout";
	public static final String ERROR_NOT_FOUND 		= "States.NotFound";
	public static final String ERROR_NOT_RETRYABLE 	= "States.NotRetryable";
	public static final String ERROR_JSON_CONVERSION= "States.JsonConversion";
		
	//STATUS
	public enum Status {
		SUCCESS,
		FAILURE,
		RETRY
	}
	
	//HANDLE RESOURCE
	/**
	 * 
	 * @param parameters
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Status handle(T parameters, StateContext context) throws Exception;
	
	//INPUT FILTER 
	public interface InputHandler<T> {
		public T onInput(StateContext context);
	}
	
	//OUTPUT FILTER
	public interface OutputHandler {
		public StateTransition.Success onOutput(StateContext context);
	}
	
	//HANDLE FAILURE
	public interface FailureHandler {
		StateTransition onFailure(StateContext context, Exception ex);
	}
	
	//HANDLE RETRY
	public interface RetryHandler {
		StateTransition onRetry(StateContext context);
	}
}
