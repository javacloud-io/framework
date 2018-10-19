package io.javacloud.framework.flow;
/**
 * 
 * @author ho
 *
 */
public interface StateFunction extends StateHandler,
	StateHandler.Success, StateHandler.Failure, StateHandler.Retry {

}
