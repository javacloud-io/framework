package io.javacloud.framework.flow;
/**
 * 
 * @author ho
 *
 */
public interface StateFunction extends StateHandler<Object>, StateHandler.InputHandler<Object>, StateHandler.OutputHandler,
	StateHandler.FailureHandler, StateHandler.RetryHandler {
	/**
	 * Generic InputHandler need type for conversion
	 * @return
	 */
	public Class<?> getParametersType();
}
