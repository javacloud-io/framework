package javacloud.framework.flow.internal;

import java.util.Map;

import javacloud.framework.flow.StateContext;
import javacloud.framework.util.Objects;

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
	public String getFlowId() {
		return state.getFlowId();
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
