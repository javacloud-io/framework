package io.javacloud.framework.flow.spi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = StateSpec.Task.class, 	name = "Task"),
	@JsonSubTypes.Type(value = StateSpec.Pass.class, 	name = "Pass"),
	@JsonSubTypes.Type(value = StateSpec.Wait.class, 	name = "Wait"),
	@JsonSubTypes.Type(value = StateSpec.Succeed.class, name = "Succeed"),
	@JsonSubTypes.Type(value = StateSpec.Fail.class, 	name = "Fail"),
	@JsonSubTypes.Type(value = StateSpec.Choice.class, 	name = "Choice"),
	@JsonSubTypes.Type(value = StateSpec.Parallel.class,name = "Parallel")
})
public abstract class StateSpec {
	public static enum Type {
		Task,
		Pass,
		Wait,
		Succeed,
		Fail,
		Choice,
		Parallel
	}
	
	public static class Retrier implements StateTransition.Retry {
		@JsonProperty("ErrorEquals")
		private List<String> errorEquals;
		
		@JsonProperty("MaxAttempts")
		private int maxAttempts		= 0;
		
		@JsonProperty("IntervalSeconds")
		private int intervalSeconds	= 5;
		
		@JsonProperty("BackoffRate")
		private double backoffRate 	= 1.0;
		
		public Retrier() {
		}
		
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

		public List<String> getErrorEquals() {
			return errorEquals;
		}
		public Retrier withErrorEquals(List<String> errorEquals) {
			this.errorEquals = errorEquals;
			return this;
		}
		public Retrier withErrorEquals(String... errorEquals) {
			this.errorEquals = Objects.asList(errorEquals);
			return this;
		}
	}
	
	public static class Catcher implements StateTransition.Success {
		@JsonProperty("ErrorEquals")
		private List<String> errorEquals;
		
		@JsonProperty("Result")
		private Object result;
		
		@JsonProperty("Output")
		private Object output;
		
		@JsonProperty("Next")
		private String next;
		public Catcher() {
		}
		
		@Override
		public boolean isEnd() {
			return false;
		}
		@Override
		public String getNext() {
			return next;
		}
		public Catcher withNext(String next) {
			this.next = next;
			return this;
		}
		
		public Object getResult() {
			return result;
		}
		public Catcher withResult(Object result) {
			this.result = result;
			return this;
		}
		
		public Object getOutput() {
			return output;
		}
		public Catcher withOutput(Object output) {
			this.output = output;
			return this;
		}
		
		public List<String> getErrorEquals() {
			return errorEquals;
		}
		public Catcher withErrorEquals(List<String> errorEquals) {
			this.errorEquals = errorEquals;
			return this;
		}
		public Catcher withErrorEquals(String... errorEquals) {
			this.errorEquals = Objects.asList(errorEquals);
			return this;
		}
	}
	
	@JsonProperty("Type")
	private Type 	type;
	
	@JsonProperty("Comment")
	private String comment;
	
	@JsonProperty("Input")
	private Object	input;
	
	//DEPRECATED, USING Input INSTEAD
	@JsonProperty("InputPath")
	private String	inputPath;
	
	@JsonProperty("Result")
	private Object	result;
	
	//DEPRECATED, USING Result INSTEAD
	@JsonProperty("ResultPath")
	private String	resultPath;
	
	@JsonProperty("Output")
	private Object	output;
	
	//DEPRECATED, USING Output INSTEAD
	@JsonProperty("OutputPath")
	private String	ouputPath;
	
	@JsonProperty("Next")
	private String next;
	
	@JsonProperty("Retry")
	private List<Retrier> retriers;
	
	@JsonProperty("Catch")
	private List<Catcher> catchers;
	public StateSpec() {
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Object getInput() {
		return input;
	}
	public void setInput(Object input) {
		this.input = input;
	}
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public Object getOutput() {
		return output;
	}
	public void setOutput(Object output) {
		this.output = output;
	}
	
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	
	public List<Retrier> getRetriers() {
		return retriers;
	}
	public void setRetriers(List<Retrier> retriers) {
		this.retriers = retriers;
	}
	
	public List<Catcher> getCatchers() {
		return catchers;
	}
	public void setCatchers(List<Catcher> catchers) {
		this.catchers = catchers;
	}
	
	//TASK
	public static class Task extends StateSpec {
		@JsonProperty("Resource")
		private String	resource;
		
		@JsonProperty("TimeoutSeconds")
		private int timeoutSeconds;
		
		@JsonProperty("HeartbeatSeconds")
		private int heartbeatSeconds;
		
		public Task() {
		}
		public String getResource() {
			return resource;
		}
		public void setResource(String resource) {
			this.resource = resource;
		}
		
		public int getTimeoutSeconds() {
			return timeoutSeconds;
		}
		public void setTimeoutSeconds(int timeoutSeconds) {
			this.timeoutSeconds = timeoutSeconds;
		}
		
		public int getHeartbeatSeconds() {
			return heartbeatSeconds;
		}
		public void setHeartbeatSeconds(int heartbeatSeconds) {
			this.heartbeatSeconds = heartbeatSeconds;
		}
	}
	
	//PASS
	public static class Pass extends StateSpec {
		public Pass() {
		}
	}
		
	//WAIT
	public static class Wait extends StateSpec {
		@JsonProperty("Seconds")
		private int seconds;
		
		//json path or ISO string
		@JsonProperty("Timestamp")
		private String timestamp;
		public Wait() {
		}
		public int getSeconds() {
			return seconds;
		}
		public void setSeconds(int seconds) {
			this.seconds = seconds;
		}
		
		public String getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}
	}
	
	//SUCCEED
	public static class Succeed extends StateSpec {
		public Succeed() {
		}
	}
	
	//FAIL
	public static class Fail extends StateSpec {
		@JsonProperty("Error")
		private String error;
		
		@JsonProperty("Cause")
		private String cause;
		public Fail() {
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		
		public String getCause() {
			return cause;
		}
		public void setCause(String cause) {
			this.cause = cause;
		}
	}
	
	//CHOICE
	public static class Choice extends StateSpec {
		@JsonProperty("Choices")
		private List<RuleSpec> rules;
		public Choice() {
		}
		public List<RuleSpec> getRules() {
			return rules;
		}
		public void setRules(List<RuleSpec> rules) {
			this.rules = rules;
		}
	}
	
	//PARALLEL
	public static class Parallel extends StateSpec {
		@JsonProperty("Branches")
		private List<FlowSpec> branches;
		public Parallel() {
		}
		public List<FlowSpec> getBranches() {
			return branches;
		}
		public void setBranches(List<FlowSpec> branches) {
			this.branches = branches;
		}
	}
}
