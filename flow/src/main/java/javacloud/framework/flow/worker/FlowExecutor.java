package javacloud.framework.flow.worker;

import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.flow.StateFunction;
import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateFlow;
import javacloud.framework.flow.StateHandler;
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
public class FlowExecutor {
	private static final Logger logger = Logger.getLogger(FlowExecutor.class.getName());
	
	public  static final int MIN_DELAY_SECONDS = 2;
	
	private final StateFlow stateFlow;
	private final Externalizer externalizer;
	public FlowExecutor(StateFlow stateFlow, Externalizer externalizer) {
		this.stateFlow = stateFlow;
		this.externalizer = externalizer;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public <T> FlowState start(T input) {
		return start(input, null);
	}
	
	/**
	 * start -> execute
	 * @param input
	 * @param startAt;
	 * @return
	 */
	public <T> FlowState start(T input, String startAt) {
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
	protected StateTransition onExecute(FlowState state, FlowContext context) {
		StateFunction function = stateFlow.getState(state.getName());
		if(function == null) {
			return onFailure(function, context, null);
		}
		try {
			Object parameters = onInput(function, context);
			StateFunction.Status status = onHandle(function, context, parameters);
			if(status == StateHandler.Status.SUCCEEDED) {
				return onSuccess(function, context);
			} else if(status == StateHandler.Status.RETRY) {
				return	onRetry(function, context);
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
	 * @param name
	 * @return
	 */
	protected FlowState onPrepare(FlowState state, String name) {
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
			logger.log(Level.FINE, "Converting input to parameters type: {0}", type);
			try {
				parameters = new JsonConverter(externalizer).to(type).apply(parameters);
			} catch(RuntimeException ex) {
				context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateHandler.ERROR_JSON_CONVERSION);
				throw ex;
			}
		}
		return	parameters;
	}
	
	/**
	 * Handle the function and increase try-count
	 * 
	 * @param function
	 * @param context
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	protected StateHandler.Status onHandle(StateFunction function, FlowContext context, Object parameters) throws Exception {
		FlowState state = context.state;
		try {
			return	function.handle(parameters, context);
		} finally {
			state.setTryCount(state.getTryCount() + 1);
		}
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
		
		FlowState state = context.state;
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
	 * @param function
	 * @param context
	 * @return
	 */
	protected StateTransition onRetry(StateFunction function, FlowContext context) {
		StateTransition transition = function.onRetry(context);
		FlowState state = context.state;
		if(transition instanceof StateTransition.Failure) {
			state.setStatus(FlowExecution.Status.FAILED);
		}
		logger.log(Level.FINE, "Retrying state: {0}, transition: {1}", new Object[] {state.getName(), transition});
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
		FlowState state = context.state;
		//NOT FOUND STATE
		if(function == null) {
			logger.log(Level.FINE, "Not found state: {0}", state.getName());
			context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateHandler.ERROR_NOT_FOUND);
			transition = TransitionBuilder.failure();
		} else {
			transition = function.onFailure(context, ex);
		}
		
		//HANDLE FAILURE, SET DETAILS ERROR IF HAVEN'T DONE SO
		if(transition instanceof StateTransition.Failure) {
			state.setStatus(FlowExecution.Status.FAILED);
			String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
			if(ex != null && error == null) {
				error = Exceptions.getReason(ex);
				context.setAttribute(StateContext.ATTRIBUTE_ERROR, error);
				state.setStackTrace(Exceptions.getStackTrace(ex, -1));
			}
			logger.log(Level.FINE, "Failed state: {0}, error: {1}", new Object[] {state.getName(), error});
		}
		return transition;
	}
}
