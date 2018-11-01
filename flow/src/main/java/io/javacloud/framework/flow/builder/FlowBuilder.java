package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.Map;

import io.javacloud.framework.flow.StateAction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateFlow;

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
	 * @param handler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateHandler<?> handler, String next) {
		return withState(name, new ActionBuilder()
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
		return withState(name, new ActionBuilder()
								.withStateHandler(handler)
								.withRepeatHandler(repeatHandler)
								.withSuccessTransition(TransitionBuilder.success(next))
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
			states = new HashMap<>();
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
			public StateAction getState(String stepName) {
				return (states == null? null : states.get(stepName));
			}
		};
	}
}
