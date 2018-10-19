package io.javacloud.framework.flow.internal;

import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.StateMachine;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.UncheckedException;

/**
 * 
 * @author ho
 *
 */
public class FlowExecutor {
	public  static final int MIN_DELAY_SECONDS = 2;
	
	private final StateMachine stateMachine;
	public FlowExecutor(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public FlowState start(Dictionary parameters) {
		return start(parameters, null);
	}
	
	/**
	 * start -> execute
	 * @param startAt;
	 * @param parameters
	 * @return
	 */
	public FlowState start(Dictionary parameters, String startAt) {
		FlowState state = new FlowState();
		state.setAttributes(parameters == null? new Dictionary(): parameters);
		return state.reset(Objects.isEmpty(startAt) ? stateMachine.getStartAt() : startAt);
	}
	
	/**
	 * execute -[end]-> [success]
	 * 			[success] 	-> execute
	 * 			[retry]		-> [delays] -> execute
	 * 						-> [not]	-> [failure]
	 * 			[failure]	-> []
	 * @param parameters
	 * @param state
	 * @return
	 */
	public StateTransition execute(Dictionary parameters, FlowState state) {
		FlowContext context = new FlowContext(parameters, state);
		StateFunction function = stateMachine.getState(state.getName());
		try {
			StateFunction.Status status = function.handle(context);
			if(status == StateFunction.Status.SUCCESS) {
				StateTransition.Success success = function.onSuccess(context);
				state.setName(success.getNext());
				return success;
			} else if(status == StateFunction.Status.RETRY) {
				return	function.onRetry(context);
			}
			//UNKNOWN FAILURE
			return function.onFailure(context, null);
		} catch(Exception ex) {
			state.setStackTrace(UncheckedException.toStackTrace(ex));
			return function.onFailure(context, ex);
		}
	}
	
	/**
	 * 
	 * @param parameters
	 * @param state
	 */
	public void complete(Dictionary parameters, FlowState state) {
	}
	
	/**
	 * return number of SECONDS for DELAYS otherwise INDICATION AS FAILED
	 * 
	 * @param parameters
	 * @param state
	 * @param transition
	 * @return
	 */
	public int retry(Dictionary parameters, FlowState state, StateTransition.Retry transition) {
		int retryCount = state.getRetryCount();
		if(retryCount < transition.getMaxAttempts()) {
			long maxTimeout = transition.getTimeoutSeconds() * 1000L;
			//TIMEOUT => FAILED
			if(maxTimeout >= 0 && (state.getStartedAt() + maxTimeout) >= System.currentTimeMillis()) {
				return 0;
			} else {
				state.setRetryCount(retryCount + 1);
				int delaySeconds = (int)(transition.getIntervalSeconds() * Math.pow(transition.getBackoffRate(), retryCount - 1));
				return Math.min(delaySeconds, MIN_DELAY_SECONDS);
			}
		}
		return 0;
	}
}
