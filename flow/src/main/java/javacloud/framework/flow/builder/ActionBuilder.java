package javacloud.framework.flow.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javacloud.framework.flow.StateAction;
import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateHandler;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class ActionBuilder {
	private StateHandler<Object, Object> 	stateHandler;
	private StateHandler.InputHandler<Object> inputHandler;
	private StateHandler.OutputHandler	 outputHandler;
	private StateHandler.FailureHandler  failureHandler;
	private StateHandler.RetryHandler	 retryHandler;
	public ActionBuilder() {
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withStateHandler(StateHandler<?, ?> handler) {
		this.stateHandler = Objects.cast(handler);
		return this;
	}
	
	/**
	 * Handler always return a fix status PASS/FAIL
	 * @param status
	 * @return
	 */
	public ActionBuilder withStateHandler(final StateHandler.Status status) {
		return withStateHandler(new StateHandler<Object, StateHandler.Status>() {
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
	public ActionBuilder withRetryHandler(StateHandler.RetryHandler handler) {
		this.retryHandler = handler;
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
				return (inputHandler == null? context.getInput() : inputHandler.onInput(context));
			}
			@Override
			public StateHandler.Status handle(Object parameters, StateContext context) throws Exception {
				if(stateHandler == null) {
					return StateHandler.Status.SUCCEED;
				}
				//RESPECT STATUS
				Object result = stateHandler.handle(parameters, context);
				if(result instanceof StateHandler.Status) {
					return Objects.cast(result);
				}
				//SUCCESS WITH NO RESULT
				if(result == null || result instanceof Void) {
					return StateHandler.Status.SUCCEED; 
				}
				return TransitionBuilder.succeed(context, result);
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
				return (retryHandler == null? TransitionBuilder.retry(0) : retryHandler.onRetry(context));
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
	 * Assuming handler has ONE generic interface StateHandler<T> or StateHandler<X<T>>
	 * 
	 * @param handlerClass
	 * @return
	 */
	protected Class<?> getActualTypeArguments(Class<?> handlerClass) {
		Type type = ((ParameterizedType)handlerClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
		if(type instanceof Class) {
			return (Class<?>)type;
		}
		return (Class<?>)((ParameterizedType)type).getRawType();
	}
}
