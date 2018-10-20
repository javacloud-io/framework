package io.javacloud.framework.flow.internal;

import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.StateMachine;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.UncheckedException;

/**
 * start -> execute -[end]-> [success]
 * 					 [success] 	-> execute
 * 					 [retry]	-> [delays] -> execute
 * 								-> [not]	-> [failure]
 * 					 [failure]	-> []
 * @author ho
 *
 */
public class FlowHandler {
	public  static final int MIN_DELAY_SECONDS = 2;
	
	private final StateMachine stateMachine;
	private final Dictionary parameters;
	public FlowHandler(StateMachine stateMachine, Dictionary parameters) {
		this.stateMachine = stateMachine;
		this.parameters = parameters;
	}
	
	/**
	 * 
	 * @return
	 */
	public FlowState start() {
		return start(null);
	}
	
	/**
	 * start -> execute
	 * 
	 * @param startAt;
	 * @return
	 */
	public FlowState start(String startAt) {
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
	public StateTransition execute(FlowState state) {
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
			return onFailure(function, context, null);
		} catch(Exception ex) {
			return onFailure(function, context, ex);
		}
	}
	
	/**
	 * 
	 * @param state
	 */
	public void complete(FlowState state) {
	}
	
	/**
	 * return number of SECONDS for DELAYS otherwise INDICATION AS FAILED
	 * 
	 * @param state
	 * @param transition
	 * @return 0: timeout, -1: can't retry
	 */
	public int retry(FlowState state, StateTransition.Retry transition) {
		int retryCount = state.getRetryCount();
		if(retryCount < transition.getMaxAttempts()) {
			long maxTimeout = transition.getTimeoutSeconds() * 1000L;
			//TIMEOUT => FAILED [0]
			if(maxTimeout >= 0 && (state.getStartedAt() + maxTimeout) >= System.currentTimeMillis()) {
				state.setFailed(true);
				return 0;
			} else {
				state.setRetryCount(retryCount + 1);
				int delaySeconds = (int)(transition.getIntervalSeconds() * Math.pow(transition.getBackoffRate(), retryCount - 1));
				return Math.max(delaySeconds, MIN_DELAY_SECONDS);
			}
		} else {
			//FAILED[1]
			state.setFailed(true);
		}
		return -1;
	}
	
	/**
	 * 
	 * @param function
	 * @param context
	 * @param ex
	 * @return
	 */
	protected StateTransition onFailure(StateFunction function, FlowContext context, Exception ex) {
		StateTransition transition;
		//NOT FOUND STATE
		if(function == null) {
			transition = TransitionBuilder.failure();
		} else {
			transition = function.onFailure(context, ex);
		}
		
		//HANDLE FAILURE
		if(transition instanceof StateTransition.Failure) {
			context.state.setFailed(true);
			if(ex != null) {
				context.state.setStackTrace(UncheckedException.toStackTrace(ex));
			}
		}
		return transition;
	}
}