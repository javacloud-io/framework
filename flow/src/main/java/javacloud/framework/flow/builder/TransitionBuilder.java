package javacloud.framework.flow.builder;

import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateFunction;
import javacloud.framework.flow.StateTransition;

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
		return success(next, 0);
	}
	
	/**
	 * @param next
	 * @param delaySeconds
	 * @return
	 */
	public static StateTransition.Success success(final String next, final int delaySeconds) {
		return new StateTransition.Success() {
			@Override
			public int getDelaySeconds() {
				return delaySeconds;
			}
			
			@Override
			public String getNext() {
				return next;
			}
			@Override
			public String toString() {
				return (delaySeconds == 0? "Success" : "SuccessWait");
			}
		};
	}
	
	/**
	 * 
	 * @return
	 */
	public static StateTransition.Failure failure() {
		return new StateTransition.Failure(){
			@Override
			public String toString() {
				return "Failure";
			}
		};
	}
	
	/**
	 * Repeat same state with delay in seconds
	 * 
	 * @return
	 */
	public static StateTransition.Retry retry(final int delaySeconds) {
		return new StateTransition.Retry() {
			@Override
			public int getDelaySeconds() {
				return delaySeconds;
			}
			@Override
			public String toString() {
				return "Retry";
			}
		};
	}
	
	/**
	 * Success with result
	 * @param context
	 * @param result
	 * @return
	 */
	public static StateFunction.Status succeed(StateContext context, Object result) {
		context.setAttribute(StateContext.ATTRIBUTE_RESULT, result);
		return StateFunction.Status.SUCCEEDED;
	}
	
	/**
	 * 
	 * @param context
	 * @param error
	 * @return
	 */
	public static StateFunction.Status fail(StateContext context, String error) {
		context.setAttribute(StateContext.ATTRIBUTE_ERROR, error);
		return StateFunction.Status.FAILED;
	}
}
