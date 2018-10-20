package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;

/**
 * 
 * @author ho
 *
 */
public class FunctionBuilder {
	private StateHandler 		  stateHandler;
	private StateHandler.SuccessHandler  successHandler;
	private StateHandler.FailureHandler  failureHandler;
	private StateHandler.RetryHandler	  retryHandler;
	public FunctionBuilder() {
	}
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withStateHandler(StateHandler handler) {
		this.stateHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withSuccessHandler(StateHandler.SuccessHandler  handler) {
		this.successHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withFailureHandler(StateHandler.FailureHandler  handler) {
		this.failureHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withRetryHandler(StateHandler.RetryHandler handler) {
		this.retryHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param transition
	 * @return
	 */
	public FunctionBuilder withSuccessTransition(final StateTransition.Success transition) {
		return withSuccessHandler(new StateHandler.SuccessHandler() {
			@Override
			public StateTransition.Success onSuccess(StateContext context) {
				return transition;
			}
		});
	}
	
	/**
	 * 
	 * @param transition
	 * @return
	 */
	public FunctionBuilder withFailureTransition(final StateTransition.Failure transition) {
		return withFailureHandler(new StateHandler.FailureHandler() {
			@Override
			public StateTransition onFailure(StateContext context, Exception ex) {
				return transition;
			}
		});
	}
	
	/**
	 * 
	 * @param transition
	 * @return
	 */
	public FunctionBuilder withRetryHandler(final StateTransition.Retry transition) {
		return withRetryHandler(new StateHandler.RetryHandler() {
			@Override
			public StateTransition.Retry onRetry(StateContext context) {
				return transition;
			}
		});
	}
	
	/**
	 * 
	 * @return
	 */
	public StateFunction build() {
		return new StateFunction() {
			@Override
			public StateHandler.Status handle(StateContext context) throws Exception {
				return (stateHandler == null? StateHandler.Status.FAILURE : stateHandler.handle(context));
			}
			@Override
			public StateTransition.Success onSuccess(StateContext context) {
				return (successHandler == null? TransitionBuilder.success(null) : successHandler.onSuccess(context));
			}

			@Override
			public StateTransition onFailure(StateContext context, Exception ex) {
				return (failureHandler == null? TransitionBuilder.failure() : failureHandler.onFailure(context, ex));
			}

			@Override
			public StateTransition.Retry onRetry(StateContext context) {
				return (retryHandler == null? new TransitionBuilder().retry() : retryHandler.onRetry(context));
			}
		};
	}
}
