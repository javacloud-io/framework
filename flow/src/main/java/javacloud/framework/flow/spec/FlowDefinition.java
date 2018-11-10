package javacloud.framework.flow.spec;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Implement Amazon States Language https://states-language.net/spec.html with extension:
 * 
 * 1. Allows to compile Input/Result/Output as JSON not just JsonPath
 * 2. 
 * 
 * @author ho
 *
 */
public class FlowDefinition {
	@JsonProperty("Comment")
	private String comment;
	
	@JsonProperty("StartAt")
	private String startAt;
	
	@JsonProperty("States")
	private Map<String, StateDefinition> states;
	public FlowDefinition() {
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public FlowDefinition withComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public String getStartAt() {
		return startAt;
	}
	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}
	public FlowDefinition withStartAt(String startAt) {
		this.startAt = startAt;
		return this;
	}
	
	public Map<String, StateDefinition> getStates() {
		return states;
	}
	public void setStates(Map<String, StateDefinition> states) {
		this.states = states;
	}
	public FlowDefinition withState(String name, StateDefinition state) {
		if(states == null) {
			states = new LinkedHashMap<>();
		}
		states.put(name, state);
		return this;
	}
}
