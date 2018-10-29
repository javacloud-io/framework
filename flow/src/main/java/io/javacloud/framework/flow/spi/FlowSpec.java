package io.javacloud.framework.flow.spi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Implement Amazon States Language https://states-language.net/spec.html with extension:
 * 
 * 1. Allows to compile Input/Result/Output as JSON not just JsonPath
 * 2. 
 * 
 * @author ho
 *
 */
public class FlowSpec {
	@JsonProperty("Comment")
	private String comment;
	
	@JsonProperty("StartAt")
	private String startAt;
	
	@JsonProperty("States")
	@JsonDeserialize(as=LinkedHashMap.class, keyAs=String.class, contentAs=StateSpec.class)
	private Map<String, StateSpec> states;
	public FlowSpec() {
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public FlowSpec withComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public String getStartAt() {
		return startAt;
	}
	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}
	public FlowSpec withStartAt(String startAt) {
		this.startAt = startAt;
		return this;
	}
	
	public Map<String, StateSpec> getStates() {
		return states;
	}
	public void setStates(Map<String, StateSpec> states) {
		this.states = states;
	}
	public FlowSpec withState(String name, StateSpec state) {
		if(states == null) {
			states = new LinkedHashMap<>();
		}
		states.put(name, state);
		return this;
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
		
		@JsonProperty("Timestamp")
		private Object timestamp;
		public Wait() {
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
	}
	
	//CHOICE
	public static class Choice extends StateSpec {
		@JsonProperty("Choices")
		private List<RuleSpec> rules;
		public Choice() {
		}
	}
	
	//PARALLEL
	public static class Parallel extends StateSpec {
		@JsonProperty("Branches")
		private List<FlowSpec> branches;
		public Parallel() {
		}
	}
}
