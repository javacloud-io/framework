package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.spec.StateSpec;
import io.javacloud.framework.util.Objects;

/**
 * 
 * * {
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
	private Map<String, StateSpec.Retrier> retriers;
	public RetryBuilder() {
	}
	
	/**
	 * 
	 * @param retrier
	 * @param errors
	 * @return
	 */
	public RetryBuilder withRetrier(StateSpec.Retrier retrier, String... errors) {
		if(retriers == null) {
			retriers = new HashMap<>();
		}
		//ADD EQUAL ERRORS
		if(Objects.isEmpty(errors)) {
			if(Objects.isEmpty(retrier.getErrorEquals())) {
				retriers.put(StateHandler.ERROR_ALL, retrier);
			} else {
				for(String error: retrier.getErrorEquals()) {
					retriers.put(error, retrier);
				}
			}
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
	public RetryBuilder withRetriers(List<StateSpec.Retrier> retriers) {
		for(StateSpec.Retrier retrier: retriers) {
			withRetrier(retrier);
		}
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateHandler.RetryHandler build() {
		return new StateHandler.RetryHandler() {
			@Override
			public StateTransition onRetry(StateContext context) {
				String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
				StateSpec.Retrier retrier = null;
				if(error != null) {
					retrier = (retriers == null? null : retriers.get(error));
				}
				if(retrier == null && retriers != null) {
					retrier = retriers.get(StateHandler.ERROR_ALL);
				}
				return (retrier == null? TransitionBuilder.failure(): retrier);
			}
		};
	}
}
