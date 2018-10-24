package io.javacloud.framework.flow;

/**
 * 
 * @author ho
 *
 */
public interface StateHandler<T> {
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
