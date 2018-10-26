package io.javacloud.framework.flow.builder;

import java.lang.reflect.ParameterizedType;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FunctionBuilder {
	private StateHandler<Object> 		 stateHandler;
	private StateHandler.InputHandler<Object> inputHandler;
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
	public FunctionBuilder withStateHandler(StateHandler<?> handler) {
		this.stateHandler = Objects.cast(handler);
		return this;
	}
	
	/**
	 * Handler always return a fix status PASS/FAIL
	 * @param status
	 * @return
	 */
	public FunctionBuilder withStateHandler(final StateHandler.Status status) {
		return withStateHandler(new StateHandler<Object>() {
			@Override
			public Status handle(Object parameters, StateContext context) throws Exception {
				return status;
			}
		});
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public FunctionBuilder withInputHandler(StateHandler.InputHandler<?>  handler) {
		this.inputHandler = Objects.cast(handler);
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
	 * @return
	 */
	public  StateFunction build() {
		return new StateFunction() {
			@Override
			public Object onInput(StateContext context) {
				if(inputHandler == null) {
					return context.getParameters();
				}
				return inputHandler.onInput(context);
			}
			@Override
			public StateHandler.Status handle(Object parameters, StateContext context) throws Exception {
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
				return (retryHandler == null? TransitionBuilder.failure() : retryHandler.onRetry(context));
			}
			
			@Override
			public Class<?> getParametersType() {
				if(stateHandler != null) {
					ParameterizedType type = (ParameterizedType)stateHandler.getClass().getGenericInterfaces()[0];
					return (Class<?>)type.getActualTypeArguments()[0];
				} else if(inputHandler != null) {
					ParameterizedType type = (ParameterizedType)inputHandler.getClass().getGenericInterfaces()[0];
					return (Class<?>)type.getActualTypeArguments()[0];
				}
				return Object.class;
			}
		};
	}
}
