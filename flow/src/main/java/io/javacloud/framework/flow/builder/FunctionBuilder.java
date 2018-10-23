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
	private StateHandler 		 		 stateHandler;
	private StateHandler.InputHandler	 inputHandler;
	private StateHandler.OutputHandler	 outputHandler;
	private StateHandler.FailureHandler  failureHandler;
	private StateHandler.RetryHandler	 retryHandler;
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
	public FunctionBuilder withInputHandler(StateHandler.InputHandler  handler) {
		this.inputHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withOutputHandler(StateHandler.OutputHandler  handler) {
		this.outputHandler = handler;
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
		return withOutputHandler(new StateHandler.OutputHandler() {
			@Override
			public StateTransition.Success onOutput(StateContext context) {
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
			public <T> T onInput(StateContext context) {
				if(inputHandler == null) {
					return context.getParameters();
				}
				return inputHandler.onInput(context);
			}
			@Override
			public <T> StateHandler.Status handle(T parameters, StateContext context) throws Exception {
				return (stateHandler == null? StateHandler.Status.FAILURE : stateHandler.handle(parameters, context));
			}
			@Override
			public StateTransition.Success onOutput(StateContext context) {
				return (outputHandler == null? TransitionBuilder.success(null) : outputHandler.onOutput(context));
			}

			@Override
			public StateTransition onFailure(StateContext context, Exception ex) {
				return (failureHandler == null? TransitionBuilder.failure() : failureHandler.onFailure(context, ex));
			}

			@Override
			public StateTransition onRetry(StateContext context) {
				return (retryHandler == null? new TransitionBuilder().retry() : retryHandler.onRetry(context));
			}
		};
	}
}
