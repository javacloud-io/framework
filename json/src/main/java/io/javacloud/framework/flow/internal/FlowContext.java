package io.javacloud.framework.flow.internal;

import java.util.Collections;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.util.Dictionary;

/**
 * 
 * @author ho
 *
 */
public class FlowContext implements StateContext {
	private final Dictionary parameters;
	final FlowState  state;
	public FlowContext(Dictionary parameters, FlowState  state) {
		this.parameters = new Dictionary(Collections.unmodifiableMap(parameters));
		this.state		= state;
	}
	
	@Override
	public String getFlowId() {
		return state.getFlowId();
	}

	@Override
	public Dictionary getParameters() {
		return parameters;
	}

	@Override
	public Dictionary getAttributes() {
		return (state == null ? null: state.getAttributes());
	}

	@Override
	public <T> T getAttribute(String name) {
		Dictionary attributes = getAttributes();
		if(attributes == null) {
			return null;
		}
		return attributes.get(name);
	}

	@Override
	public <T> void setAttribute(String name, T attribute) {
		Dictionary attributes = getAttributes();
		if(attributes != null) {
			attributes.set(name, attribute);
		}
	}

	@Override
	public int getRetryCount() {
		return state.getRetryCount();
	}
}
