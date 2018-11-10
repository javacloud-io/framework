package javacloud.framework.flow.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

import javacloud.framework.flow.StateAction;
import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateFunction;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class ActionBuilder {
	private StateFunction<Object, Object> 	stateFunction;
	private StateFunction.InputHandler<Object> inputHandler;
	private StateFunction.OutputHandler	 outputHandler;
	private StateFunction.FailureHandler failureHandler;
	private StateFunction.RetryHandler	 retryHandler;
	public ActionBuilder() {
	}
	
	/**
	 * 
	 * @param stateFunction
	 * @return
	 */
	public <T, R> ActionBuilder withStateFunction(StateFunction<T, R> stateFunction) {
		this.stateFunction = Objects.cast(stateFunction);
		return this;
	}
	
	/**
	 * 
	 * @param function
	 * @return
	 */
	public <T, R> ActionBuilder withStateFunction(Function<T, R> function) {
		return withStateFunction(new StateFunction<T, R>() {
			@Override
			public R handle(T parameters, StateContext context) throws Exception {
				return function.apply(parameters);
			}
		});
	}
	
	/**
	 * Handler always return a fix status PASS/FAIL
	 * @param status
	 * @return
	 */
	public ActionBuilder withStateFunction(final StateFunction.Status status) {
		return withStateFunction(new StateFunction<Object, StateFunction.Status>() {
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
	public ActionBuilder withInputHandler(StateFunction.InputHandler<?>  handler) {
		this.inputHandler = Objects.cast(handler);
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withOutputHandler(StateFunction.OutputHandler  handler) {
		this.outputHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withFailureHandler(StateFunction.FailureHandler  handler) {
		this.failureHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public ActionBuilder withRetryHandler(StateFunction.RetryHandler handler) {
		this.retryHandler = handler;
		return this;
	}
	
	/**
	 * 
	 * @param transition
	 * @return
	 */
	public ActionBuilder withOutputHandler(final StateTransition.Success transition) {
		return withOutputHandler(new StateFunction.OutputHandler() {
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
	public ActionBuilder withFailureHandler(final StateTransition.Failure transition) {
		return withFailureHandler(new StateFunction.FailureHandler() {
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
			public StateFunction.Status handle(Object parameters, StateContext context) throws Exception {
				if(stateFunction == null) {
					return StateFunction.Status.SUCCEEDED;
				}
				//RESPECT STATUS
				Object result = stateFunction.handle(parameters, context);
				if(result instanceof StateFunction.Status) {
					return Objects.cast(result);
				}
				//SUCCESS WITH NO RESULT
				if(result == null || result instanceof Void) {
					return StateFunction.Status.SUCCEEDED; 
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
				if(stateFunction != null) {
					return getActualParametersType(stateFunction.getClass());
				} else if(inputHandler != null) {
					return getActualParametersType(inputHandler.getClass());
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
	protected Class<?> getActualParametersType(Class<?> handlerClass) {
		Type type = ((ParameterizedType)handlerClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
		if(type instanceof Class) {
			return (Class<?>)type;
		}
		return (Class<?>)((ParameterizedType)type).getRawType();
	}
}
