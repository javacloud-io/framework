package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.Map;

import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateFlow;

/**
 * 
 * @author hvho
 *
 */
public class FlowBuilder {
	private String startAt;
	private Map<String, StateFunction> states;
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
	public FlowBuilder withState(String name, StateHandler<?> handler, String next) {
		return withState(name, new FunctionBuilder()
								.withStateHandler(handler)
								.withSuccessTransition(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param handler
	 * @param repeatHandler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateHandler<?> handler, StateHandler.RepeatHandler repeatHandler, String next) {
		return withState(name, new FunctionBuilder()
								.withStateHandler(handler)
								.withRepeatHandler(repeatHandler)
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
		if(states == null) {
			states = new HashMap<>();
		}
		states.put(name, function);
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateFlow build() {
		return new StateFlow() {
			@Override
			public String getStartAt() {
				return startAt;
			}
			
			@Override
			public StateFunction getState(String stepName) {
				return (states == null? null : states.get(stepName));
			}
		};
	}
}
