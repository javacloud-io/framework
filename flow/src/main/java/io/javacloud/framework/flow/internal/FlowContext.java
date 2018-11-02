package io.javacloud.framework.flow.internal;

import java.util.Map;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FlowContext implements StateContext {
	final FlowState  state;
	public FlowContext(FlowState  state) {
		this.state	= state;
	}
	
	@Override
	public String getExecutionId() {
		return state.getExecutionId();
	}

	@Override
	public <T> T getInput() {
		return Objects.cast(state.getInput());
	}

	@Override
	public <T> T getAttribute(String name) {
		Map<String, Object> attributes = state.getAttributes();
		if(attributes == null) {
			return null;
		}
		return Objects.cast(attributes.get(name));
	}

	@Override
	public <T> void setAttribute(String name, T attribute) {
		Map<String, Object> attributes = state.getAttributes();
		if(attributes != null) {
			attributes.put(name, attribute);
		}
	}

	@Override
	public int getTryCount() {
		return state.getTryCount();
	}
}
