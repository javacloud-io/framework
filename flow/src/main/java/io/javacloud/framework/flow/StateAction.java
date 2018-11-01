package io.javacloud.framework.flow;

/**
 * 
 * @author ho
 *
 */
public interface StateAction extends StateHandler<Object>, StateHandler.InputHandler<Object>, StateHandler.OutputHandler,
	StateHandler.FailureHandler, StateHandler.RetryHandler {
	/**
	 * Generic InputHandler need type for conversion
	 * @return
	 */
	public Class<?> getParametersType();
	
	/**
	 * Default action timeout
	 * 
	 * @return
	 */
	default public int getTimeoutSeconds() {
		return 120;
	}
	
	/**
	 * Default heartbeat to keep alive
	 * 
	 * @return
	 */
	default public int getHeartbeatSeconds() {
		return 10;
	}
}
