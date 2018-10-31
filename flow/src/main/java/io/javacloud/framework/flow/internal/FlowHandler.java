package io.javacloud.framework.flow.internal;

import java.io.IOException;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.flow.StateFlow;
import io.javacloud.framework.json.internal.JsonConverter;
import io.javacloud.framework.util.Dictionary;
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
	 * execute -[end]-> [success]
	 * 			[success] 	-> execute
	 * 			[repeat]	-> [delays] -> execute
	 * 						-> [not]	-> [failure]
	 * 			[failure]	-> []
	 * @param parameters
	 * @param state
	 * @return
	 */
	public StateTransition execute(FlowState state) {
		FlowContext context = new FlowContext(state);
		StateFunction function = stateFlow.getState(state.getName());
		try {
			Object parameters = onInput(function, context);
			StateFunction.Status status = function.handle(parameters, context);
			if(status == StateFunction.Status.SUCCESS) {
				return onSuccess(function, context);
			} else if(status == StateFunction.Status.REPEAT) {
				return	onResume(function, context);
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
	 * 
	 * @param state
	 * @param name
	 * @return
	 */
	protected FlowState onPrepare(FlowState state, String name) {
		//AN EMPTY INPUT IF NOT PROVIDED
		Object input = state.getInput();
		if(input == null) {
			input = new Dictionary();
		}
		state.setInput(input);
		state.setAttributes(new Dictionary());
		
		//RESET OTHERS
		state.setName(name);
		state.setStartedAt(System.currentTimeMillis());
		state.setRunCount(0);
		state.setStackTrace(null);
		return state;
	}
	
	/**
	 * Filter the input and AUTO convert the parameters for handler if not applicable.
	 * 
	 * @param function
	 * @param context
	 * @return
	 * @throws Exception
	 */
	protected Object onInput(StateFunction function, StateContext context) throws Exception {
		Object parameters = function.onInput(context);
		Class<?> type = function.getParametersType();
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
	 * OUTPUT = {RESULT + INPUT}
	 * 
	 * @param function
	 * @param context
	 * @return
	 */
	protected StateTransition onSuccess(StateFunction function, FlowContext context) {
		StateTransition.Success transition = function.onOutput(context);
		
		//PREPARE NEXT STEP (OUTPUT -> INPUT)
		FlowState state = context.state;
		if(!transition.isEnd()) {
			state.setInput(state.output());
			onPrepare(state, transition.getNext());
		} else {
			state.setRunCount(state.getRunCount() + 1);
		}
		return transition;
	}
	
	/**
	 * Resume to control the state
	 * 
	 * @param function
	 * @param context
	 * @return
	 */
	protected StateTransition onResume(StateFunction function, FlowContext context) {
		StateTransition transition = function.onResume(context);
		FlowState state = context.state;
		state.setRunCount(state.getRunCount() + 1);
		return transition;
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
			context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateHandler.ERROR_NOT_FOUND);
			transition = TransitionBuilder.failure();
		} else {
			transition = function.onFailure(context, ex);
		}
		
		//HANDLE FAILURE
		if(transition instanceof StateTransition.Failure) {
			context.state.setFailed(true);
			if(ex != null) {
				//IF ERROR CODE IS NOT SET => USING CLASS NAME
				if(context.getAttribute(StateContext.ATTRIBUTE_ERROR) == null) {
					context.setAttribute(StateContext.ATTRIBUTE_ERROR, UncheckedException.findReason(ex));
				}
				context.state.setStackTrace(UncheckedException.toStackTrace(ex));
			}
		}
		return transition;
	}
}
