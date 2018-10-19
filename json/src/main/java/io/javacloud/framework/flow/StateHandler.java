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
	public interface Success {
		StateTransition.Success onSuccess(StateContext context);
	}
	
	//HANDLE FAILURE
	public interface Failure {
		StateTransition onFailure(StateContext context, Exception ex);
	}
	
	//HANDLE RETRY
	public interface Retry {
		StateTransition.Retry onRetry(StateContext context);
	}
}
