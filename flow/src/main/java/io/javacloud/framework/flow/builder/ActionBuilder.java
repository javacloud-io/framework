package io.javacloud.framework.flow.builder;

import java.lang.reflect.ParameterizedType;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateAction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class ActionBuilder {
	private StateHandler<Object> 		 stateHandler;
	private StateHandler.InputHandler<Object> inputHandler;
	private StateHandler.OutputHandler	 outputHandler;
	private StateHandler.FailureHandler  failureHandler;
	private StateHandler.RepeatHandler	 repeatHandler;
	public ActionBuilder() {
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withStateHandler(StateHandler<?> handler) {
		this.stateHandler = Objects.cast(handler);
		return this;
	}
	
	/**
	 * Handler always return a fix status PASS/FAIL
	 * @param status
	 * @return
	 */
	public ActionBuilder withStateHandler(final StateHandler.Status status) {
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
	public ActionBuilder withInputHandler(StateHandler.InputHandler<?>  handler) {
		this.inputHandler = Objects.cast(handler);
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withOutputHandler(StateHandler.OutputHandler  handler) {
		this.outputHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withFailureHandler(StateHandler.FailureHandler  handler) {
		this.failureHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withRepeatHandler(StateHandler.RepeatHandler handler) {
		this.repeatHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param transition
	 * @return
	 */
	public ActionBuilder withSuccessTransition(final StateTransition.Success transition) {
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
	public ActionBuilder withFailureTransition(final StateTransition.Failure transition) {
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
	public  StateAction build() {
		return new StateAction() {
			@Override
			public Object onInput(StateContext context) {
				if(inputHandler == null) {
					return context.getInput();
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
			public StateTransition onResume(StateContext context) {
				return (repeatHandler == null? TransitionBuilder.repeat(0) : repeatHandler.onResume(context));
			}
			
			@Override
			public Class<?> getParametersType() {
				if(stateHandler != null) {
					return getActualTypeArguments(stateHandler.getClass());
				} else if(inputHandler != null) {
					return getActualTypeArguments(inputHandler.getClass());
				}
				return Object.class;
			}
		};
	}
	
	/**
	 * Assuming handler has ONE generic interface
	 * @param handlerClass
	 * @return
	 */
	protected Class<?> getActualTypeArguments(Class<?> handlerClass) {
		ParameterizedType type = (ParameterizedType)handlerClass.getGenericInterfaces()[0];
		return (Class<?>)type.getActualTypeArguments()[0];
	}
}
