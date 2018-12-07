package javacloud.framework.flow.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

import javacloud.framework.flow.StateFunction;
import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateHandler;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FunctionBuilder {
	private StateHandler<Object, Object>	stateHandler;
	private StateHandler.InputHandler<Object> inputHandler;
	private StateHandler.OutputHandler	 outputHandler;
	private StateHandler.FailureHandler  failureHandler;
	private StateHandler.RetryHandler	 retryHandler;
	public FunctionBuilder() {
	}
	
	/**
	 * 
	 * @param stateHandler
	 * @return
	 */
	public <T, R> FunctionBuilder withStateHandler(StateHandler<T, R> stateHandler) {
		this.stateHandler = Objects.cast(stateHandler);
		return this;
	}
	
	/**
	 * 
	 * @param function
	 * @return
	 */
	public <T, R> FunctionBuilder withStateHandler(Function<T, R> function) {
		return withStateHandler(new StateHandler<T, R>() {
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
	public FunctionBuilder withStateHandler(final StateHandler.Status status) {
		return withStateHandler(new StateHandler<Object, StateHandler.Status>() {
			@Override
			public Status handle(Object parameters, StateContext context) throws Exception {
				return status;
			}
		});
	}
	
	/**
	 * 
	 * @param inputHandler
	 * @return
	 */
	public FunctionBuilder withInputHandler(StateHandler.InputHandler<?>  inputHandler) {
		this.inputHandler = Objects.cast(inputHandler);
		return this;
	}
	
	/**
	 * 
	 * @param outputHandler
	 * @return
	 */
	public FunctionBuilder withOutputHandler(StateHandler.OutputHandler  outputHandler) {
		this.outputHandler = outputHandler;
		return this;
	}
	
	/**
	 * 
	 * @param failureHandler
	 * @return
	 */
	public FunctionBuilder withFailureHandler(StateHandler.FailureHandler failureHandler) {
		this.failureHandler = failureHandler;
		return this;
	}
	
	/**
	 * 
	 * @param retryHandler
	 * @return
	 */
	public FunctionBuilder withRetryHandler(StateHandler.RetryHandler retryHandler) {
		this.retryHandler = retryHandler;
		return this;
	}
	
	/**
	 * 
	 * @param transition
	 * @return
	 */
	public FunctionBuilder withOutputHandler(final StateTransition.Success transition) {
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
	public FunctionBuilder withFailureHandler(final StateTransition.Failure transition) {
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
				return (inputHandler == null? context.getInput() : inputHandler.onInput(context));
			}
			@Override
			public StateHandler.Status handle(Object parameters, StateContext context) throws Exception {
				if(stateHandler == null) {
					return StateHandler.Status.SUCCEEDED;
				}
				//RESPECT STATUS
				Object result = stateHandler.handle(parameters, context);
				if(result instanceof StateHandler.Status) {
					return Objects.cast(result);
				}
				//SUCCESS WITH NO RESULT
				if(result == null || result instanceof Void) {
					return StateHandler.Status.SUCCEEDED; 
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
					return getActualParametersType(stateHandler.getClass());
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
