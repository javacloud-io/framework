package javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateHandler;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.flow.spi.RetrierSpec;
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
	private Map<String, RetrierSpec> retriers;
	public RetryBuilder() {
	}
	
	/**
	 * 
	 * @param retrier
	 * @param errors
	 * @return
	 */
	public RetryBuilder withRetrier(RetrierSpec retrier) {
		if(retriers == null) {
			retriers = new HashMap<>();
		}
		
		//ADD EQUAL ERRORS
		List<String> errors = retrier.getErrorEquals();
		if(Objects.isEmpty(errors)) {
			retriers.put(StateHandler.ERROR_ALL, retrier);
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
	public RetryBuilder withRetriers(List<RetrierSpec> retriers) {
		if(!Objects.isEmpty(retriers)) {
			for(RetrierSpec retrier: retriers) {
				withRetrier(retrier);
			}
		}
		return this;
	}
	
	/**
	 * return a repeat builder with re-trier definition
	 * @return
	 */
	public StateHandler.RetryHandler build() {
		return new StateHandler.RetryHandler() {
			@Override
			public StateTransition onRetry(StateContext context) {
				String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
				RetrierSpec retrier;
				if(error != null) {
					retrier = (retriers == null? null : retriers.get(error));
				} else {
					retrier = null;
				}
				//USING DEFAULT
				if(retrier == null && retriers != null) {
					retrier = retriers.get(StateHandler.ERROR_ALL);
				}
				
				//CAN'T RE-TRY
				if(retrier == null) {
					context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateHandler.ERROR_NOT_RETRYABLE);
					return	TransitionBuilder.failure();
				} else if(context.getTryCount() > retrier.getMaxAttempts()) {
					context.setAttribute(StateContext.ATTRIBUTE_ERROR, StateHandler.ERROR_TIMEOUT);
					return	TransitionBuilder.failure();
				}
				
				//CALCULATE FINAL DELAYS
				int delaySeconds = (int)(retrier.getIntervalSeconds() * Math.pow(retrier.getBackoffRate(), context.getTryCount() - 1));
				return TransitionBuilder.retry(delaySeconds);
			}
		};
	}
}
