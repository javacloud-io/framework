package javacloud.framework.flow;

/**
 * An executable action
 * 
 * @author ho
 *
 */
public interface StateFunction extends StateHandler<Object, StateHandler.Status>, StateHandler.InputHandler<Object>,
	StateHandler.OutputHandler, StateHandler.FailureHandler, StateHandler.RetryHandler {
	/**
	 * Generic InputHandler need type for conversion
	 * @return
	 */
	public Class<?> getParametersType();
	
	/**
	 * Default function timeout
	 * 
	 * @return
	 */
	default public int getTimeoutSeconds() {
		return 120;
	}
	
	/**
	 * Default heart beat to keep function alive
	 * 
	 * @return
	 */
	default public int getHeartbeatSeconds() {
		return 10;
	}
}
