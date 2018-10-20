package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.Map;

import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateMachine;

/**
 * 
 * @author hvho
 *
 */
public class FlowBuilder {
	private String startAt;
	private Map<String, StateFunction> states = new HashMap<>();
	public FlowBuilder() {
	}
	
	/**
	 * 
	 * @param startAt
	 * @return
	 */
	public FlowBuilder withStartAt(String startAt) {
		this.startAt = startAt;
		return this;
	}
	
	/**
	 * 
	 * @param name
	 * @param handler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateHandler handler, String next) {
		return withState(name, new FunctionBuilder()
								.withStateHandler(handler)
								.withSuccessTransition(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param handler
	 * @param retryHandler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateHandler handler, StateHandler.RetryHandler retryHandler, String next) {
		return withState(name, new FunctionBuilder()
								.withStateHandler(handler)
								.withRetryHandler(retryHandler)
								.withSuccessTransition(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param function
	 * @return
	 */
	public FlowBuilder withState(String name, StateFunction function) {
		states.put(name, function);
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateMachine build() {
		return new StateMachine() {
			@Override
			public String getStartAt() {
				return startAt;
			}
			
			@Override
			public StateFunction getState(String stepName) {
				return states.get(stepName);
			}
		};
	}
}