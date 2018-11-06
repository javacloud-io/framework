package javacloud.framework.flow.internal;

import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.flow.StateContext;
import javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FlowContext implements StateContext {
	private static final Logger logger = Logger.getLogger(StateContext.class.getName());
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
	
	/**
	 * FIXME: Persist and correctly log
	 */
	@Override
	public void log(Level level, String message, Object... arguments) {
		if(!logger.isLoggable(level)) {
			return;
		}
		//BASIC TO ADVANCE FORMAT
		if(Objects.isEmpty(arguments)) {
			logger.log(level, message);
		} else if(arguments[arguments.length - 1] instanceof Throwable) {
			if(arguments.length > 1) {
				message = MessageFormat.format(message, arguments);
			}
			logger.log(level, message, (Throwable)arguments[arguments.length - 1]);
		} else {
			logger.log(level, message, arguments);
		}
	}
}
