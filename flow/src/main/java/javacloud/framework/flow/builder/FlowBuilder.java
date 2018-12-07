package javacloud.framework.flow.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import javacloud.framework.flow.StateFunction;
import javacloud.framework.flow.StateFlow;
import javacloud.framework.flow.StateHandler;

/**
 * Helper to build workflow with function and next
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
	 * @param stateHandler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateHandler<?, ?> stateHandler, String next) {
		return withState(name, new FunctionBuilder()
								.withStateHandler(stateHandler)
								.withOutputHandler(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param stateHandler
	 * @param retryHandler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateHandler<?, ?> stateHandler, StateHandler.RetryHandler retryHandler, String next) {
		return withState(name, new FunctionBuilder()
								.withStateHandler(stateHandler)
								.withRetryHandler(retryHandler)
								.withOutputHandler(TransitionBuilder.success(next))
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
			states = new LinkedHashMap<>();
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
			public StateFunction getState(String name) {
				return (states == null? null : states.get(name));
			}
		};
	}
}
