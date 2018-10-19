package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.StateTransition.Success;

/**
 * 
 * @author ho
 *
 */
public class FunctionBuilder {
	private StateHandler 		  stateHandler;
	private StateHandler.Success  successHandler;
	private StateHandler.Failure  failureHandler;
	private StateHandler.Retry	  retryHandler;
	public FunctionBuilder() {
	}
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withResourceHandler(StateHandler handler) {
		this.stateHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withSuccessHandler(StateHandler.Success  handler) {
		this.successHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withFailureHandler(StateHandler.Failure  handler) {
		this.failureHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withRetryHandler(StateHandler.Retry handler) {
		this.retryHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param transition
	 * @return
	 */
	public FunctionBuilder withSuccessTransition(final StateTransition.Success transition) {
		return withSuccessHandler(new StateHandler.Success() {
			@Override
			public Success onSuccess(StateContext context) {
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
		return withFailureHandler(new StateHandler.Failure() {
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
		return withRetryHandler(new StateHandler.Retry() {
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
