package io.javacloud.framework.flow.internal;

import java.io.IOException;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateAction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.flow.StateFlow;
import io.javacloud.framework.json.internal.JsonConverter;
import io.javacloud.framework.util.Externalizer;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.UncheckedException;

/**
 * start -> execute -[end]-> [success]
 * 					 [success] 	-> execute
 * 					 [repeat]	-> [delays] -> execute
 * 								-> [not]	-> [failure]
 * 					 [failure]	-> []
 * @author ho
 *
 */
public class FlowHandler {
	public  static final int MIN_DELAY_SECONDS = 2;
	
	private final StateFlow stateFlow;
	private final Externalizer externalizer;
	public FlowHandler(StateFlow stateFlow, Externalizer externalizer) {
		this.stateFlow 	  = stateFlow;
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
	 * @param input
	 * @param startAt;
	 * @return
	 */
	public FlowState start(Object input, String startAt) {
		FlowState state = new FlowState();
		state.setInput(input);
		return onPrepare(state, Objects.isEmpty(startAt) ? stateFlow.getStartAt() : startAt);
	}
	
	/**
	 * Execute with default flow context
	 * @param state
	 * @return
	 */
	public StateTransition execute(FlowState state) {
		FlowContext context = new FlowContext(state);
		return onExecute(state, context);
	}
	
	/**
	 * Flip the final PARAMS as OUTPUT
	 * 
	 * @param state
	 */
	public void complete(FlowState state) {
	}
	
	/**
	 * execute -[end]-> [success]
	 * 			[success] 	-> execute
	 * 			[repeat]	-> [delays] -> execute
	 * 						-> [not]	-> [failure]
	 * 			[failure]	-> []
	 * @param state
	 * @param context
	 * @return
	 */
	protected StateTransition onExecute(FlowState state, FlowContext context) {
		StateAction action = stateFlow.getState(state.getName());
		if(action == null) {
			return onFailure(action, context, null);
		}
		try {
			Object parameters = onInput(action, context);
			StateAction.Status status = onHandle(action, context, parameters);
			if(status == StateHandler.Status.SUCCESS) {
				return onSuccess(action, context);
			} else if(status == StateHandler.Status.RETRY) {
				return	onRetry(action, context);
			}
			
			//UNKNOWN FAILURE
			return onFailure(action, context, null);
		} catch(Exception ex) {
			return onFailure(action, context, ex);
		}
	}
	
	/**
	 * 
	 * @param state
	 * @param name
	 * @return
	 */
	protected FlowState onPrepare(FlowState state, String name) {
		//AN EMPTY INPUT IF NOT PROVIDED
		Object input = state.getInput();
		if(input == null) {
			input = Objects.asMap();
		}
		state.setInput(input);
		state.setAttributes(Objects.asMap());
		
		//RESET OTHERS
		state.setName(name);
		state.setStartedAt(System.currentTimeMillis());
		state.setTryCount(0);
		state.setStackTrace(null);
		return state;
	}
	
	/**
	 * Filter the input and AUTO convert the parameters for handler if not applicable.
	 * 
	 * @param action
	 * @param context
	 * @return
	 * @throws Exception
	 */
	protected Object onInput(StateAction action, StateContext context) throws Exception {
		Object parameters = action.onInput(context);
		Class<?> type = action.getParametersType();
		//PARAMETERS CONVERSION!!!
		if(parameters != null && externalizer != null && !type.isInstance(parameters)) {
			try {
				parameters = new JsonConverter(externalizer).convert(parameters, type);
			} catch(IOException ex) {
				context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateHandler.ERROR_JSON_CONVERSION);
				throw ex;
			}
		}
		return	parameters;
	}
	
	/**
	 * Handle the action and increase try-count
	 * 
	 * @param action
	 * @param context
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	protected StateHandler.Status onHandle(StateAction action, FlowContext context, Object parameters) throws Exception {
		try {
			return	action.handle(parameters, context);
		} finally {
			FlowState state = context.state;
			state.setTryCount(state.getTryCount() + 1);
		}
	}
	
	/**
	 * OUTPUT = {RESULT + INPUT}
	 * 
	 * @param action
	 * @param context
	 * @return
	 */
	protected StateTransition onSuccess(StateAction action, FlowContext context) {
		StateTransition.Success transition = action.onOutput(context);
		
		//PREPARE NEXT STEP (OUTPUT -> INPUT)
		if(!transition.isEnd()) {
			FlowState state = context.state;
			state.setInput(state.output());
			onPrepare(state, transition.getNext());
		}
		return transition;
	}
	
	/**
	 * Retry to control the state 
	 * 
	 * @param action
	 * @param context
	 * @return
	 */
	protected StateTransition onRetry(StateAction action, FlowContext context) {
		StateTransition transition = action.onRetry(context);
		if(transition instanceof StateTransition.Failure) {
			context.state.setFailed(true);
		}
		return transition;
	}
	
	/**
	 * 
	 * @param action
	 * @param context
	 * @param ex
	 * @return
	 */
	protected StateTransition onFailure(StateAction action, FlowContext context, Exception ex) {
		StateTransition transition;
		//NOT FOUND STATE
		if(action == null) {
			context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateHandler.ERROR_NOT_FOUND);
			transition = TransitionBuilder.failure();
		} else {
			transition = action.onFailure(context, ex);
		}
		
		//HANDLE FAILURE
		if(transition instanceof StateTransition.Failure) {
			context.state.setFailed(true);
			//SET DETAILS ERROR IF HAVEN'T DONE SO
			if(ex != null && context.getAttribute(StateContext.ATTRIBUTE_ERROR) == null) {
				context.setAttribute(StateContext.ATTRIBUTE_ERROR, UncheckedException.findReason(ex));
				context.state.setStackTrace(UncheckedException.toStackTrace(ex));
			}
		}
		return transition;
	}
}
