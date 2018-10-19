package io.javacloud.framework.flow;
/**
 * 
 * @author ho
 *
 */
public interface StateFunction extends StateHandler,
	StateHandler.SuccessHandler, StateHandler.FailureHandler, StateHandler.RetryHandler {

}
