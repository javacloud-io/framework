package io.javacloud.framework.flow;
/**
 * 
 * @author ho
 *
 */
public interface StateHandler {
	public enum Status {
		SUCCESS,
		FAILURE,
		RETRY
	}
	
	//HANDLE RESOURCE
	public Status handle(StateContext context) throws Exception;
	
	//HANDLE SUCCESS
	public interface SuccessHandler {
		StateTransition.Success onSuccess(StateContext context);
	}
	
	//HANDLE FAILURE
	public interface FailureHandler {
		StateTransition onFailure(StateContext context, Exception ex);
	}
	
	//HANDLE RETRY
	public interface RetryHandler {
		StateTransition.Retry onRetry(StateContext context);
	}
}
