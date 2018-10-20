package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class TransitionBuilder {
	private int timeoutSeconds	= -1;
	private int maxAttempts		= 0;
	private int intervalSeconds	= 5;
	private double backoffRate 	= 1.0;
	public TransitionBuilder() {
	}
	/**
	 * 
	 * @param timeoutSeconds
	 * @return
	 */
	public TransitionBuilder withTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
		return	this;
	}
	
	/**
	 * 
	 * @param intervalSeconds
	 * @return
	 */
	public TransitionBuilder withDelaySeconds(int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
		return this;
	}
	
	/**
	 * 
	 * @param maxAttempts
	 * @return
	 */
	public TransitionBuilder withMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}
	
	/**
	 * 
	 * @param backoffRate
	 * @return
	 */
	public TransitionBuilder withBackoffRate(double backoffRate) {
		this.backoffRate = backoffRate;
		return this;
	}
	
	/**
	 * Transition to next step
	 * 
	 * @param nextStep
	 * @return
	 */
	public StateTransition.Retry retry() {
		return new StateTransition.Retry() {
			@Override
			public boolean isEnd() {
				return false;
			}
			
			@Override
			public int getTimeoutSeconds() {
				return timeoutSeconds;
			}
			
			@Override
			public int getMaxAttempts() {
				return maxAttempts;
			}
			
			@Override
			public int getIntervalSeconds() {
				return intervalSeconds;
			}
			
			@Override
			public double getBackoffRate() {
				return backoffRate;
			}
		};
	}
	
	/**
	 * 
	 * @param next
	 * @return
	 */
	public static StateTransition.Success success(final String next) {
		return new StateTransition.Success() {
			@Override
			public boolean isEnd() {
				return Objects.isEmpty(next);
			}
			
			@Override
			public String getNext() {
				return next;
			}
		};
	}
	
	/**
	 * 
	 * @return
	 */
	public static StateTransition.Failure failure() {
		return new StateTransition.Failure() {
			@Override
			public boolean isEnd() {
				return true;
			}
		};
	}
}
