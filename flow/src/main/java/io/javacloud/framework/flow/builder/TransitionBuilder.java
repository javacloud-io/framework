package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class TransitionBuilder {
	public TransitionBuilder() {
	}
	
	/**
	 * 
	 * @param next
	 * @return
	 */
	public static StateTransition.Success success(final String next) {
		return new StateTransition.Success() {
			@Override
			public boolean isEnd() {
				return Objects.isEmpty(next);
			}
			
			@Override
			public String getNext() {
				return next;
			}
		};
	}
	
	/**
	 * Success with result
	 * @param context
	 * @param result
	 * @return
	 */
	public static StateHandler.Status success(StateContext context, Object result) {
		context.setAttribute(StateContext.ATTRIBUTE_RESULT, result);
		return StateHandler.Status.SUCCESS;
	}
	
	/**
	 * 
	 * @return
	 */
	public static StateTransition.Failure failure() {
		return new StateTransition.Failure() {
			@Override
			public boolean isEnd() {
				return true;
			}
		};
	}
	
	/**
	 * 
	 * @param context
	 * @param error
	 * @return
	 */
	public static StateHandler.Status failure(StateContext context, String error) {
		context.setAttribute(StateContext.ATTRIBUTE_ERROR, error);
		return StateHandler.Status.FAILURE;
	}
}
