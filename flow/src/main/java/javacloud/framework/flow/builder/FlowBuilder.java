package javacloud.framework.flow.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import javacloud.framework.flow.StateAction;
import javacloud.framework.flow.StateFlow;
import javacloud.framework.flow.StateFunction;

/**
 * 
 * @author hvho
 *
 */
public class FlowBuilder {
	private String startAt;
	private Map<String, StateAction> states;
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
	 * @param function
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateFunction<?, ?> function, String next) {
		return withState(name, new ActionBuilder()
								.withStateFunction(function)
								.withOutputHandler(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param function
	 * @param retryHandler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateFunction<?, ?> function, StateFunction.RetryHandler retryHandler, String next) {
		return withState(name, new ActionBuilder()
								.withStateFunction(function)
								.withRetryHandler(retryHandler)
								.withOutputHandler(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param action
	 * @return
	 */
	public FlowBuilder withState(String name, StateAction action) {
		if(states == null) {
			states = new LinkedHashMap<>();
		}
		states.put(name, action);
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
			public StateAction getState(String name) {
				return (states == null? null : states.get(name));
			}
		};
	}
}
