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
public abstract class RuleSpec implements StateTransition.Success {
	@JsonProperty("Next")
	private String next;
	public RuleSpec() {
	}
	
	@Override
	public boolean isEnd() {
		return false;
	}

	@Override
	public String getNext() {
		return next;
	}
	
	//
	//Operator: Values
	public static abstract class Condition {
		@JsonProperty("Variable")
		private String variable;
		protected Condition() {
		}
	}
	
	public static class StringEquals extends Condition {
		@JsonProperty("StringEquals")
		private String value;
		public StringEquals() {
		}
	}
	public static class StringLessThan extends Condition {
		@JsonProperty("StringLessThan")
		private String value;
		public StringLessThan() {
		}
	}
	public static class StringGreaterThan extends Condition {
		@JsonProperty("StringGreaterThan")
		private String value;
		public StringGreaterThan() {
		}
	}
	public static class StringLessThanEquals extends Condition {
		@JsonProperty("StringLessThanEquals")
		private String value;
		public StringLessThanEquals() {
		}
	}
	public static class StringGreaterThanEquals extends Condition {
		@JsonProperty("StringGreaterThanEquals")
		private String value;
		public StringGreaterThanEquals() {
		}
	}
	
	public static class NumericEquals extends Condition {
		@JsonProperty("NumericEquals")
		private Number value;
		public NumericEquals() {
		}
	}
	public static class NumericLessThan extends Condition {
		@JsonProperty("NumericLessThan")
		private Number value;
		public NumericLessThan() {
		}
	}
	public static class NumericGreaterThan extends Condition {
		@JsonProperty("NumericGreaterThan")
		private String value;
		public NumericGreaterThan() {
		}
	}
	public static class NumericLessThanEquals extends Condition {
		@JsonProperty("NumericLessThanEquals")
		private Number value;
		public NumericLessThanEquals() {
		}
	}
	public static class NumericGreaterThanEquals extends Condition {
		@JsonProperty("NumericGreaterThanEquals")
		private Number value;
		public NumericGreaterThanEquals() {
		}
	}
	
	public static class BooleanEquals extends Condition {
		@JsonProperty("BooleanEquals")
		private Boolean value;
		public BooleanEquals() {
		}
	}
	
	public static class TimestampEquals extends Condition {
		@JsonProperty("TimestampEquals")
		private Date value;
		public TimestampEquals() {
		}
	}
	public static class TimestampLessThan extends Condition {
		@JsonProperty("TimestampLessThan")
		private Date value;
		public TimestampLessThan() {
		}
	}
	public static class TimestampGreaterThan extends Condition {
		@JsonProperty("TimestampGreaterThan")
		private Date value;
		public TimestampGreaterThan() {
		}
	}
	public static class TimestampLessThanEquals extends Condition {
		@JsonProperty("TimestampLessThanEquals")
		private Date value;
		public TimestampLessThanEquals() {
		}
	}
	public static class TimestampGreaterThanEquals extends Condition {
		@JsonProperty("TimestampGreaterThanEquals")
		private Date value;
		public TimestampGreaterThanEquals() {
		}
	}
	
	//LOGICAL
	public static class Not extends RuleSpec {
		@JsonProperty("Not")
		private Condition condition;
		public Not() {
		}
	}
	public static class And extends RuleSpec {
		@JsonProperty("And")
		private List<Condition> conditions;
		public And() {
		}
	}
	public static class Or extends RuleSpec {
		@JsonProperty("Or")
		private List<Condition> conditions;
		public Or() {
		}
	}
}
