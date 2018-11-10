package javacloud.framework.flow.spec;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import javacloud.framework.flow.StateTransition;
import javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class CatcherDefinition implements StateTransition.Success {
	@JsonProperty("ErrorEquals")
	private List<String> errorEquals;
	
	@JsonProperty("Result")
	private Object result;
	
	@JsonProperty("Output")
	private Object output;
	
	@JsonProperty("Next")
	private String next;
	public CatcherDefinition() {
	}
	
	public String getNext() {
		return next;
	}
	public CatcherDefinition withNext(String next) {
		this.next = next;
		return this;
	}
	
	public Object getResult() {
		return result;
	}
	public CatcherDefinition withResult(Object result) {
		this.result = result;
		return this;
	}
	
	public Object getOutput() {
		return output;
	}
	public CatcherDefinition withOutput(Object output) {
		this.output = output;
		return this;
	}
	
	public List<String> getErrorEquals() {
		return errorEquals;
	}
	public CatcherDefinition withErrorEquals(List<String> errorEquals) {
		this.errorEquals = errorEquals;
		return this;
	}
	public CatcherDefinition withErrorEquals(String... errorEquals) {
		this.errorEquals = Objects.asList(errorEquals);
		return this;
	}
}