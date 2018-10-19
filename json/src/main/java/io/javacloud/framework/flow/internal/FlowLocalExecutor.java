package io.javacloud.framework.flow.internal;

import java.util.concurrent.TimeUnit;

import io.javacloud.framework.flow.StateMachine;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.internal.FlowState;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FlowLocalExecutor extends FlowExecutor {
	public FlowLocalExecutor(StateMachine stateMachine) {
		super(stateMachine);
	}
	
	/**
	 * 
	 */
	@Override
	public FlowState start(Dictionary parameters, String startAt) {
		FlowState state = super.start(parameters, startAt);
		try {
			execute(parameters, state);
		} finally {
			complete(parameters, state);
		}
		return state;
	}
	
	/**
	 * Execute to completion
	 */
	@Override
	public StateTransition execute(Dictionary parameters, FlowState state) {
		StateTransition transition = super.execute(parameters, state);
		if(transition.isEnd()) {
			return transition;
		}
		if(transition instanceof StateTransition.Retry) {
			int delaySeconds = retry(parameters, state, (StateTransition.Retry)transition);
			Objects.sleep(delaySeconds, TimeUnit.MILLISECONDS);
			return	execute(parameters, state);
		}
		//TAKE NAP & RE-TRY
		Objects.sleep(MIN_DELAY_SECONDS, TimeUnit.MILLISECONDS);
		return	execute(parameters, state);
	}
}
