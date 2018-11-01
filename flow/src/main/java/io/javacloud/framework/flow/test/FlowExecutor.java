package io.javacloud.framework.flow.test;
import java.util.concurrent.TimeUnit;

import io.javacloud.framework.flow.StateFlow;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.internal.FlowHandler;
import io.javacloud.framework.flow.internal.FlowState;
import io.javacloud.framework.util.Codecs;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Externalizer;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FlowExecutor extends FlowHandler {
	public FlowExecutor(StateFlow stateFlow, Externalizer externalizer) {
		super(stateFlow, externalizer);
	}
	public FlowExecutor(StateFlow stateFlow) {
		this(stateFlow, null);
	}
	/**
	 * 
	 */
	@Override
	public FlowState start(Object parameters, String startAt) {
		FlowState state = super.start(parameters, startAt);
		state.setExecutionId(Codecs.randomID());
		try {
			execute(state);
		} finally {
			complete(state);
		}
		return state;
	}
	
	/**
	 * Execute to completion
	 */
	@Override
	public StateTransition execute(FlowState state) {
		StateTransition transition = super.execute(state);
		//FAILURE/SUCCESS
		if(transition.isEnd()) {
			return transition;
		}
		
		//TAKE NAP & RESUME
		if(transition instanceof StateTransition.Retry) {
			int delaySeconds = ((StateTransition.Retry)transition).getDelaySeconds();
			if(delaySeconds > 0) {
				Objects.sleep(delaySeconds, TimeUnit.SECONDS);
			}
			return	execute(state);
		}
		
		//TAKE A SCALING NAP & EXECUTE NEXT
		int delaySeconds = ((StateTransition.Success)transition).getDelaySeconds();
		if(delaySeconds < MIN_DELAY_SECONDS) {
			delaySeconds = MIN_DELAY_SECONDS;
		}
		Objects.sleep(MIN_DELAY_SECONDS, TimeUnit.MILLISECONDS);
		return	execute(state);
	}
	
	/**
	 * 
	 * @param stateFlow
	 * @param parameters
	 * @return
	 */
	public static FlowState start(StateFlow stateFlow, Dictionary parameters) {
		return new FlowExecutor(stateFlow).start(parameters);
	}
}
