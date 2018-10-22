package io.javacloud.framework.flow.test;
import java.util.concurrent.TimeUnit;

import io.javacloud.framework.flow.StateMachine;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.flow.internal.FlowHandler;
import io.javacloud.framework.flow.internal.FlowState;
import io.javacloud.framework.util.Codecs;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class FlowExecutor extends FlowHandler {
	public FlowExecutor(StateMachine stateMachine) {
		super(stateMachine);
	}
	
	/**
	 * 
	 */
	@Override
	public FlowState start(Object parameters, String startAt) {
		FlowState state = super.start(parameters, startAt);
		state.setFlowId(Codecs.randomID());
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
		if(transition.isEnd()) {
			return transition;
		}
		if(transition instanceof StateTransition.Retry) {
			int delaySeconds = retry(state, (StateTransition.Retry)transition);
			if(delaySeconds <= 0) {
				return TransitionBuilder.failure();
			}
			//TAKE NAP & RE-TRY
			Objects.sleep(delaySeconds, TimeUnit.MILLISECONDS);
			return	execute(state);
		}
		//TAKE NAP & EXECUTE NEXT
		Objects.sleep(MIN_DELAY_SECONDS, TimeUnit.MILLISECONDS);
		return	execute(state);
	}
	
	/**
	 * 
	 * @param stateMachine
	 * @param parameters
	 * @return
	 */
	public static FlowState run(StateMachine stateMachine, Dictionary parameters) {
		return new FlowExecutor(stateMachine).start(parameters);
	}
}
