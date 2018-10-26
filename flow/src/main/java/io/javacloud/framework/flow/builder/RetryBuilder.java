package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.Map;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
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
	public static class Retrier implements StateTransition.Retry {
		private int timeoutSeconds	= -1;
		private int maxAttempts		= 0;
		private int intervalSeconds	= 5;
		private double backoffRate 	= 1.0;
		
		@Override
		public boolean isEnd() {
			return false;
		}
		@Override
		public int getMaxAttempts() {
			return maxAttempts;
		}
		public Retrier withMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
			return this;
		}
		
		@Override
		public int getIntervalSeconds() {
			return intervalSeconds;
		}
		public Retrier withIntervalSeconds(int intervalSeconds) {
			this.intervalSeconds = intervalSeconds;
			return this;
		}
		
		@Override
		public double getBackoffRate() {
			return backoffRate;
		}
		public Retrier withBackoffRate(int backoffRate) {
			this.backoffRate = backoffRate;
			return this;
		}
		
		@Override
		public int getTimeoutSeconds() {
			return timeoutSeconds;
		}
		public Retrier withTimeoutSeconds(int timeoutSeconds) {
			this.timeoutSeconds = timeoutSeconds;
			return this;
		}
	}
	private Map<String, Retrier> retriers;
	public RetryBuilder() {
	}
	
	/**
	 * 
	 * @param retrier
	 * @param errors
	 * @return
	 */
	public RetryBuilder withRetrier(Retrier retrier, String... errors) {
		if(retriers == null) {
			retriers = new HashMap<>();
		}
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
	 * @return
	 */
	public StateHandler.RetryHandler build() {
		return new StateHandler.RetryHandler() {
			@Override
			public StateTransition onRetry(StateContext context) {
				String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
				Retrier retrier = null;
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
