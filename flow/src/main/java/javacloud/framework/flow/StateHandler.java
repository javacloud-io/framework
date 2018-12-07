package javacloud.framework.flow;

/**
 * 
 * @author ho
 *
 * @param <T>
 * @param <R>
 */
public interface StateHandler<T, R> {
	//BUILT-IN ERRORS
	public static final String ERROR_ALL 	   		= "States.ALL";
	public static final String ERROR_TIMEOUT 		= "States.Timeout";
	public static final String ERROR_NOT_FOUND 		= "States.NotFound";
	public static final String ERROR_NOT_RETRYABLE 	= "States.NotRetryable";
	public static final String ERROR_JSON_CONVERSION= "States.JsonConversion";
		
	//STATUS
	public enum Status {
		SUCCEEDED,
		FAILED,
		RETRY
	}
	
	/**
	 * Using Status as R for flow control otherwise treat as return type
	 * 
	 * @param parameters
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public R handle(T parameters, StateContext context) throws Exception;
	
	/**
	 *  Filter the parameters prior to handle()
	 *  
	 */
	public interface InputHandler<T> {
		public T onInput(StateContext context);
	}
	
	/**
	 * Filter out put prior to transition to next state
	 *
	 */
	public interface OutputHandler {
		public StateTransition.Success onOutput(StateContext context);
	}
	
	/**
	 * 
	 *
	 */
	public interface FailureHandler {
		public StateTransition onFailure(StateContext context, Exception ex);
	}
	
	/**
	 * 
	 *
	 */
	public interface RetryHandler {
		public StateTransition onRetry(StateContext context);
	}
}
