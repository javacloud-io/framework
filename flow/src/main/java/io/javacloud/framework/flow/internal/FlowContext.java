package io.javacloud.framework.flow.internal;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FlowContext implements StateContext {
	final FlowState  state;
	public FlowContext(FlowState  state) {
		this.state		= state;
	}
	
	@Override
	public String getFlowId() {
		return state.getFlowId();
	}

	@Override
	public <T> T getParameters() {
		return Objects.cast(state.getParameters());
	}

	@Override
	public <T> T getAttribute(String name) {
		Dictionary attributes = state.getAttributes();
		if(attributes == null) {
			return null;
		}
		return attributes.get(name);
	}

	@Override
	public <T> void setAttribute(String name, T attribute) {
		Dictionary attributes = state.getAttributes();
		if(attributes != null) {
			attributes.set(name, attribute);
		}
	}

	@Override
	public int getRetryCount() {
		return state.getRetryCount();
	}
}
