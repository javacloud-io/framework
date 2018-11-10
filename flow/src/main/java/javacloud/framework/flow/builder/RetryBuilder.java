package javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateFunction;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.flow.spec.RetrierDefinition;
import javacloud.framework.util.Objects;

/**
 * To handle re-try on ERROR or LOOP condition
 * 
 * {
 * 		"Retry":[
 * 			{ "ErrorEquals": ["a", "b"],
 * 			  "MaxAttempts": 10
 * 			}
 * 		]
 * }
 * 
 * @author ho
 *
 */
public class RetryBuilder {
	private Map<String, RetrierDefinition> retriers;
	public RetryBuilder() {
	}
	
	/**
	 * 
	 * @param retrier
	 * @param errors
	 * @return
	 */
	public RetryBuilder withRetrier(RetrierDefinition retrier) {
		if(retriers == null) {
			retriers = new HashMap<>();
		}
		
		//ADD EQUAL ERRORS
		List<String> errors = retrier.getErrorEquals();
		if(Objects.isEmpty(errors)) {
			retriers.put(StateFunction.ERROR_ALL, retrier);
		} else {
			for(String error: errors) {
				retriers.put(error, retrier);
			}
		}
		return this;
	}
	
	/**
	 * 
	 * @param retriers
	 * @return
	 */
	public RetryBuilder withRetriers(List<RetrierDefinition> retriers) {
		if(!Objects.isEmpty(retriers)) {
			for(RetrierDefinition retrier: retriers) {
				withRetrier(retrier);
			}
		}
		return this;
	}
	
	/**
	 * return a repeat builder with re-trier definition
	 * @return
	 */
	public StateFunction.RetryHandler build() {
		return new StateFunction.RetryHandler() {
			@Override
			public StateTransition onRetry(StateContext context) {
				String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
				RetrierDefinition retrier;
				if(error != null) {
					retrier = (retriers == null? null : retriers.get(error));
				} else {
					retrier = null;
				}
				//USING DEFAULT
				if(retrier == null && retriers != null) {
					retrier = retriers.get(StateFunction.ERROR_ALL);
				}
				
				//CAN'T RE-TRY
				if(retrier == null) {
					context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateFunction.ERROR_NOT_RETRYABLE);
					return	TransitionBuilder.failure();
				} else if(context.getTryCount() > retrier.getMaxAttempts()) {
					context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateFunction.ERROR_TIMEOUT);
					return	TransitionBuilder.failure();
				}
				
				//CALCULATE FINAL DELAYS
				int delaySeconds = (int)(retrier.getIntervalSeconds() * Math.pow(retrier.getBackoffRate(), context.getTryCount() - 1));
				return TransitionBuilder.retry(delaySeconds);
			}
		};
	}
}
