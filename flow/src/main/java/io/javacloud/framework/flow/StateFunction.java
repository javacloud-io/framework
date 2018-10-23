package io.javacloud.framework.flow;
/**
 * 
 * @author ho
 *
 */
public interface StateFunction extends StateHandler, StateHandler.InputHandler, StateHandler.OutputHandler,
	StateHandler.FailureHandler, StateHandler.RetryHandler {

}
