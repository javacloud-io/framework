package javacloud.framework.flow.spi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
	
	//TASK
	public static class Task extends StateSpec {
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
		private List<RetrierSpec> retriers;
		
		@JsonProperty("Catch")
		private List<CatcherSpec> catchers;
		
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
		
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}

		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
		}
		
		public List<RetrierSpec> getRetriers() {
			return retriers;
		}
		public void setRetriers(List<RetrierSpec> retriers) {
			this.retriers = retriers;
		}
		
		public List<CatcherSpec> getCatchers() {
			return catchers;
		}
		public void setCatchers(List<CatcherSpec> catchers) {
			this.catchers = catchers;
		}
	}
	
	//PASS
	public static class Pass extends StateSpec {
		@JsonProperty("Next")
		private String next;
		
		@JsonProperty("End")
		private boolean end;
		
		public Pass() {
		}
		
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}
		
		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
		}
	}
		
	//WAIT
	public static class Wait extends StateSpec {
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
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}

		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
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
	
	//SUCCEED TERMINATE
	public static class Succeed extends StateSpec {
		public Succeed() {
		}
	}
	
	//FAIL TERMINATE
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
		
		@JsonProperty("Default")
		private String next;
		public Choice() {
		}
		public List<RuleSpec> getRules() {
			return rules;
		}
		public void setRules(List<RuleSpec> rules) {
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
	public static class Parallel extends StateSpec {
		@JsonProperty("Next")
		private String next;
		
		@JsonProperty("End")
		private boolean end;
		
		@JsonProperty("Branches")
		private List<FlowSpec> branches;
		public Parallel() {
		}
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}
		
		public boolean isEnd() {
			return end;
		}
		public void setEnd(boolean end) {
			this.end = end;
		}
		
		public List<FlowSpec> getBranches() {
			return branches;
		}
		public void setBranches(List<FlowSpec> branches) {
			this.branches = branches;
		}
	}
	
	//LOOP
}