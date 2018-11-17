package javacloud.framework.flow.spec;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javacloud.framework.flow.StateTransition;

/**
 * https://states-language.net/spec.html#state-type-table
 * 
 * @author ho
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = StateDefinition.Task.class, 		name = "Task"),
	@JsonSubTypes.Type(value = StateDefinition.Pass.class, 		name = "Pass"),
	@JsonSubTypes.Type(value = StateDefinition.Wait.class, 		name = "Wait"),
	@JsonSubTypes.Type(value = StateDefinition.Succeed.class, 	name = "Succeed"),
	@JsonSubTypes.Type(value = StateDefinition.Fail.class, 		name = "Fail"),
	@JsonSubTypes.Type(value = StateDefinition.Choice.class, 	name = "Choice"),
	@JsonSubTypes.Type(value = StateDefinition.Parallel.class,	name = "Parallel")
})
public abstract class StateDefinition {
	//TYPE OF STATE
	public static enum Type {
		@JsonProperty("Task")
		TASK,
		
		@JsonProperty("Task")
		PASS,
		
		@JsonProperty("Wait")
		WAIT,
		
		@JsonProperty("Succeed")
		SUCCEED,
		
		@JsonProperty("Fail")
		FAIL,
		
		@JsonProperty("Choice")
		CHOICE,
		
		@JsonProperty("Parallel")
		PARALLEL
	}
	
	@JsonProperty("Type")
	private Type 	type;
	
	@JsonProperty("Comment")
	private String comment;
	
	@JsonProperty("Input")
	private Object	input;
	
	@JsonProperty("Result")
	private Object	result;
	
	@JsonProperty("Output")
	private Object	output;
	
	public StateDefinition() {
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
	
	//TASK
	public static class Task extends StateDefinition implements StateTransition.Success {
		@JsonProperty("Resource")
		private String	resource;
		
		@JsonProperty("TimeoutSeconds")
		private int timeoutSeconds;
		
		@JsonProperty("HeartbeatSeconds")
		private int heartbeatSeconds;
		
		@JsonProperty("Next")
		private String next;
		
		@JsonProperty("End")
		private boolean end;
		
		@JsonProperty("Retry")
		private List<RetrierDefinition> retriers;
		
		@JsonProperty("Catch")
		private List<CatcherDefinition> catchers;
		
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
		@Override
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}
		@Override
		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
		}
		
		public List<RetrierDefinition> getRetriers() {
			return retriers;
		}
		public void setRetriers(List<RetrierDefinition> retriers) {
			this.retriers = retriers;
		}
		
		public List<CatcherDefinition> getCatchers() {
			return catchers;
		}
		public void setCatchers(List<CatcherDefinition> catchers) {
			this.catchers = catchers;
		}
	}
	
	//PASS
	public static class Pass extends StateDefinition implements StateTransition.Success {
		@JsonProperty("Next")
		private String next;
		
		@JsonProperty("End")
		private boolean end;
		
		public Pass() {
		}
		@Override
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}
		@Override
		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
		}
	}
		
	//WAIT
	public static class Wait extends StateDefinition implements StateTransition.Success {
		@JsonProperty("Next")
		private String next;
		
		@JsonProperty("End")
		private boolean end;
		
		@JsonProperty("Seconds")
		private int seconds;
		
		//json path or ISO string
		@JsonProperty("Timestamp")
		private String timestamp;
		public Wait() {
		}
		@Override
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}
		@Override
		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
		}
		@Override
		public int getDelaySeconds() {
			return seconds;
		}
		public void setDelaySeconds(int seconds) {
			this.seconds = seconds;
		}
		
		public String getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}
	}
	
	//SUCCEED TERMINATE
	public static class Succeed extends StateDefinition {
		public Succeed() {
		}
	}
	
	//FAIL TERMINATE
	public static class Fail extends StateDefinition {
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
	public static class Choice extends StateDefinition {
		@JsonProperty("Choices")
		private List<RuleDefinition> rules;
		
		@JsonProperty("Default")
		private String next;
		public Choice() {
		}
		public List<RuleDefinition> getRules() {
			return rules;
		}
		public void setRules(List<RuleDefinition> rules) {
			this.rules = rules;
		}
		
		public String getDefault() {
			return next;
		}
		public void setDefault(String next) {
			this.next = next;
		}
	}
	
	//PARALLEL
	public static class Parallel extends StateDefinition implements StateTransition.Success {
		@JsonProperty("Next")
		private String next;
		
		@JsonProperty("End")
		private boolean end;
		
		@JsonProperty("Branches")
		private List<FlowDefinition> branches;
		public Parallel() {
		}
		
		@Override
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}
		@Override
		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
		}
		
		public List<FlowDefinition> getBranches() {
			return branches;
		}
		public void setBranches(List<FlowDefinition> branches) {
			this.branches = branches;
		}
	}
	
	//LOOP
}
