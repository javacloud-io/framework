package io.javacloud.framework.flow.spi;

import java.util.LinkedHashMap;
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
}
