package javacloud.framework.flow;

/**
 * An executable action
 * 
 * @author ho
 *
 */
public interface StateAction extends StateFunction<Object, StateFunction.Status>, StateFunction.InputHandler<Object>,
	StateFunction.OutputHandler, StateFunction.FailureHandler, StateFunction.RetryHandler {
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
