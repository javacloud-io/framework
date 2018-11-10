package javacloud.framework.flow.spec;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class RetrierDefinition {
	@JsonProperty("ErrorEquals")
	private List<String> errorEquals;
	
	@JsonProperty("MaxAttempts")
	private int maxAttempts		= 3;
	
	@JsonProperty("IntervalSeconds")
	private int intervalSeconds	= 2;
	
	@JsonProperty("BackoffRate")
	private double backoffRate 	= 1.0;
	
	public RetrierDefinition() {
	}
	
	public int getMaxAttempts() {
		return maxAttempts;
	}
	public RetrierDefinition withMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}
	
	public int getIntervalSeconds() {
		return intervalSeconds;
	}
	public RetrierDefinition withIntervalSeconds(int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
		return this;
	}
	
	public double getBackoffRate() {
		return backoffRate;
	}
	public RetrierDefinition withBackoffRate(double backoffRate) {
		this.backoffRate = backoffRate;
		return this;
	}

	public List<String> getErrorEquals() {
		return errorEquals;
	}
	public RetrierDefinition withErrorEquals(List<String> errorEquals) {
		this.errorEquals = errorEquals;
		return this;
	}
	public RetrierDefinition withErrorEquals(String... errorEquals) {
		this.errorEquals = Objects.asList(errorEquals);
		return this;
	}
	
	@Override
	public String toString() {
		return "Retry: " + maxAttempts + ", " + intervalSeconds + ", " + backoffRate;
	}
}