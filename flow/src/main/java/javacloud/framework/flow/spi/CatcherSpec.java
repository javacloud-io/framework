package javacloud.framework.flow.spi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class CatcherSpec {
	@JsonProperty("ErrorEquals")
	private List<String> errorEquals;
	
	@JsonProperty("Result")
	private Object result;
	
	@JsonProperty("Output")
	private Object output;
	
	@JsonProperty("Next")
	private String next;
	public CatcherSpec() {
	}
	
	public String getNext() {
		return next;
	}
	public CatcherSpec withNext(String next) {
		this.next = next;
		return this;
	}
	
	public Object getResult() {
		return result;
	}
	public CatcherSpec withResult(Object result) {
		this.result = result;
		return this;
	}
	
	public Object getOutput() {
		return output;
	}
	public CatcherSpec withOutput(Object output) {
		this.output = output;
		return this;
	}
	
	public List<String> getErrorEquals() {
		return errorEquals;
	}
	public CatcherSpec withErrorEquals(List<String> errorEquals) {
		this.errorEquals = errorEquals;
		return this;
	}
	public CatcherSpec withErrorEquals(String... errorEquals) {
		this.errorEquals = Objects.asList(errorEquals);
		return this;
	}
}