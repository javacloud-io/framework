package io.javacloud.framework.flow.spi;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.javacloud.framework.flow.StateTransition;

/**
 * 
 * @author ho
 *
 */
public abstract class ChoiceRule implements StateTransition.Success {
	@JsonProperty("Next")
	private String next;
	public ChoiceRule() {
	}
	
	@Override
	public boolean isEnd() {
		return false;
	}

	@Override
	public String getNext() {
		return next;
	}
	
	//Operator: Values
	public static class Condition {
		@JsonProperty("Variable")
		private String variable;
	}
	
	public static class StringEquals extends Condition {
		@JsonProperty("StringEquals")
		private String value;
	}
	public static class StringLessThan extends Condition {
		@JsonProperty("StringLessThan")
		private String value;
	}
	public static class StringGreaterThan extends Condition {
		@JsonProperty("StringGreaterThan")
		private String value;
	}
	public static class StringLessThanEquals extends Condition {
		@JsonProperty("StringLessThanEquals")
		private String value;
	}
	public static class StringGreaterThanEquals extends Condition {
		@JsonProperty("StringGreaterThanEquals")
		private String value;
	}
	
	public static class NumericEquals extends Condition {
		@JsonProperty("NumericEquals")
		private Number value;
	}
	public static class NumericLessThan extends Condition {
		@JsonProperty("NumericLessThan")
		private Number stringEquals;
	}
	public static class NumericGreaterThan extends Condition {
		@JsonProperty("NumericGreaterThan")
		private String value;
	}
	public static class NumericLessThanEquals extends Condition {
		@JsonProperty("NumericLessThanEquals")
		private Number value;
	}
	public static class NumericGreaterThanEquals extends Condition {
		@JsonProperty("NumericGreaterThanEquals")
		private Number value;
	}
	
	public static class BooleanEquals extends Condition {
		@JsonProperty("BooleanEquals")
		private Boolean value;
	}
	
	public static class TimestampEquals extends Condition {
		@JsonProperty("TimestampEquals")
		private Date value;
	}
	public static class TimestampLessThan extends Condition {
		@JsonProperty("TimestampLessThan")
		private Date value;
	}
	public static class TimestampGreaterThan extends Condition {
		@JsonProperty("TimestampGreaterThan")
		private Date value;
	}
	public static class TimestampLessThanEquals extends Condition {
		@JsonProperty("TimestampLessThanEquals")
		private Date value;
	}
	public static class TimestampGreaterThanEquals extends Condition {
		@JsonProperty("TimestampGreaterThanEquals")
		private Date value;
	}
	
	//LOGICAL
	public static class Not extends ChoiceRule {
		@JsonProperty("Not")
		private Condition condition;
	}
	public static class And extends ChoiceRule {
		@JsonProperty("And")
		private List<Condition> conditions;
	}
	public static class Or extends ChoiceRule {
		@JsonProperty("Or")
		private List<Condition> conditions;
	}
}
