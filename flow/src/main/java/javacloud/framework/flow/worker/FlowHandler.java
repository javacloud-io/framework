package javacloud.framework.flow.worker;

import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.flow.StateAction;
import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateFlow;
import javacloud.framework.flow.StateFunction;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.flow.builder.TransitionBuilder;
import javacloud.framework.flow.spi.FlowExecution;
import javacloud.framework.io.Externalizer;
import javacloud.framework.json.internal.JsonConverter;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;

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
	private static final Logger logger = Logger.getLogger(FlowHandler.class.getName());
	
	public  static final int MIN_DELAY_SECONDS = 2;
	
	private final StateFlow stateFlow;
	private final Externalizer externalizer;
	public FlowHandler(StateFlow stateFlow, Externalizer externalizer) {
		this.stateFlow = stateFlow;
		this.externalizer = externalizer;
	}
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public <T> StateExecution start(T parameters) {
		return start(parameters, null);
	}
	
	/**
	 * start -> execute
	 * @param input
	 * @param startAt;
	 * @return
	 */
	public <T> StateExecution start(T input, String startAt) {
		StateExecution state = new StateExecution();
		state.setInput(input);
		return onPrepare(state, Objects.isEmpty(startAt) ? stateFlow.getStartAt() : startAt);
	}
	
	/**
	 * Execute with default flow context
	 * @param state
	 * @return
	 */
	public StateTransition execute(StateExecution state) {
		ActionContext context = new ActionContext(state);
		return onExecute(state, context);
	}
	
	/**
	 * Flip the final PARAMS as OUTPUT
	 * 
	 * @param state
	 */
	public void complete(StateExecution state) {
		logger.log(Level.FINE, "Completed execution: {0}", state.getExecutionId());
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
	protected StateTransition onExecute(StateExecution state, ActionContext context) {
		StateAction action = stateFlow.getState(state.getName());
		if(action == null) {
			return onFailure(action, context, null);
		}
		try {
			Object parameters = onInput(action, context);
			StateAction.Status status = onHandle(action, context, parameters);
			if(status == StateFunction.Status.SUCCEEDED) {
				return onSuccess(action, context);
			} else if(status == StateFunction.Status.RETRY) {
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
	protected StateExecution onPrepare(StateExecution state, String name) {
		//AN EMPTY INPUT IF NOT PROVIDED
		logger.log(Level.FINE, "Preparing state: {0}", name);
		Object input = state.getInput();
		if(input == null) {
			input = Objects.asMap();
		}
		state.setInput(input);
		state.setAttributes(Objects.asMap());
		
		//RESET OTHERS
		state.setStatus(FlowExecution.Status.INPROGRESS);
		state.setName(name);
		state.setStartUTC(System.currentTimeMillis());
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
			logger.log(Level.FINE, "Converting input to parameters type: {0}", type);
			try {
				parameters = new JsonConverter(externalizer).toConverter(type).apply(parameters);
			} catch(RuntimeException ex) {
				context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateFunction.ERROR_JSON_CONVERSION);
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
	protected StateFunction.Status onHandle(StateAction action, ActionContext context, Object parameters) throws Exception {
		StateExecution state = context.state;
		try {
			return	action.handle(parameters, context);
		} finally {
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
	protected StateTransition onSuccess(StateAction action, ActionContext context) {
		StateTransition.Success transition = action.onOutput(context);
		
		StateExecution state = context.state;
		logger.log(Level.FINE, "Succeed state: {0}, transition to: {1}", new Object[] {state.getName(), transition.getNext()});
		
		//PREPARE NEXT STEP (OUTPUT -> INPUT)
		if(!transition.isEnd()) {
			state.setInput(state.output());
			onPrepare(state, transition.getNext());
		} else {
			state.setStatus(FlowExecution.Status.SUCCEEDED);
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
	protected StateTransition onRetry(StateAction action, ActionContext context) {
		StateTransition transition = action.onRetry(context);
		StateExecution state = context.state;
		if(transition instanceof StateTransition.Failure) {
			state.setStatus(FlowExecution.Status.FAILED);
		}
		logger.log(Level.FINE, "Retrying state: {0}, transition: {1}", new Object[] {state.getName(), transition});
		return transition;
	}
	
	/**
	 * 
	 * @param action
	 * @param context
	 * @param ex
	 * @return
	 */
	protected StateTransition onFailure(StateAction action, ActionContext context, Exception ex) {
		StateTransition transition;
		StateExecution state = context.state;
		//NOT FOUND STATE
		if(action == null) {
			logger.log(Level.FINE, "Not found state: {0}", state.getName());
			context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateFunction.ERROR_NOT_FOUND);
			transition = TransitionBuilder.failure();
		} else {
			transition = action.onFailure(context, ex);
		}
		
		//HANDLE FAILURE, SET DETAILS ERROR IF HAVEN'T DONE SO
		if(transition instanceof StateTransition.Failure) {
			state.setStatus(FlowExecution.Status.FAILED);
			String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
			if(ex != null && error == null) {
				error = Exceptions.findReason(ex);
				context.setAttribute(StateContext.ATTRIBUTE_ERROR, error);
				state.setStackTrace(Exceptions.toStackTrace(ex));
			}
			logger.log(Level.FINE, "Failed state: {0}, error: {1}", new Object[] {state.getName(), error});
		}
		return transition;
	}
}
