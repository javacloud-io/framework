package io.javacloud.framework.flow.internal;

import java.io.IOException;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.StateMachine;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.json.internal.JsonConverter;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Externalizer;
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
	private final Externalizer externalizer;
	public FlowHandler(StateMachine stateMachine, Externalizer externalizer) {
		this.stateMachine = stateMachine;
		this.externalizer = externalizer;
	}
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public FlowState start(Object parameters) {
		return start(parameters, null);
	}
	
	/**
	 * start -> execute
	 * 
	 * @param startAt;
	 * @return
	 */
	public FlowState start(Object parameters, String startAt) {
		FlowState state = new FlowState();
		state.setParameters(parameters);
		return onPrepare(state, Objects.isEmpty(startAt) ? stateMachine.getStartAt() : startAt);
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
		FlowContext context = new FlowContext(state);
		StateFunction function = stateMachine.getState(state.getName());
		try {
			Object parameters = onInput(function, context);
			StateFunction.Status status = function.handle(parameters, context);
			if(status == StateFunction.Status.SUCCESS) {
				return onSuccess(function, context);
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
	 * Flip the final PARAMS as OUTPUT
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
	 * @param state
	 * @param name
	 * @return
	 */
	protected FlowState onPrepare(FlowState state, String name) {
		//AN EMPTY INPUT IF NOT PROVIDED
		Object parameters = state.getParameters();
		if(parameters == null) {
			parameters = new Dictionary();
		}
		state.setParameters(parameters);
		state.setAttributes(new Dictionary());
		
		//RESET OTHERS
		state.setName(name);
		state.setStartedAt(System.currentTimeMillis());
		state.setRetryCount(0);
		state.setStackTrace(null);
		return state;
	}
	
	/**
	 * Filter the input and AUTO convert the parameters for handler if not applicable.
	 * 
	 * @param function
	 * @param context
	 * @return
	 */
	protected Object onInput(StateFunction function, StateContext context) throws IOException {
		Object parameters = function.onInput(context);
		Class<?> type = function.getHandlerType();
		//PARAMETERS CONVERSION!!!
		if(parameters != null && externalizer != null && !type.isInstance(parameters)) {
			parameters = new JsonConverter(externalizer).convert(parameters, type);
		}
		return	parameters;
	}
	
	/**
	 * OUTPUT = {RESULT + INPUT}
	 * 
	 * @param function
	 * @param context
	 * @return
	 */
	protected StateTransition onSuccess(StateFunction function, FlowContext context) {
		StateTransition.Success success = function.onOutput(context);
		
		//PREPARE NEXT STEP (OUTPUT -> INPUT)
		if(!success.isEnd()) {
			FlowState state = context.state;
			state.setParameters(state.result());
			onPrepare(state, success.getNext());
		}
		return success;
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
