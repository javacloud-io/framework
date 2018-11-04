package javacloud.framework.flow.spec;

import java.util.Date;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
@JsonDeserialize(builder = RuleSpec.RuleBuilder.class)
public abstract class RuleSpec {
	@JsonProperty("Next")
	private String next;
	protected RuleSpec(String next) {
		this.next = next;
	}
	
	public String getNext() {
		return next;
	}
	
	//
	//Operator: Values
	@JsonDeserialize(builder = ConditionBuilder.class)
	public static abstract class Condition {
		@JsonProperty("Variable")
		private String variable;
		protected Condition(String variable) {
			this.variable = variable;
		}
		public String getVariable() {
			return variable;
		}
		public abstract Object getValue();
	}
	
	public static class BooleanEquals extends Condition {
		@JsonProperty("BooleanEquals")
		private Boolean value;
		public BooleanEquals(String variable, boolean value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Boolean getValue() {
			return value;
		}
	}
	
	public static class NumericEquals extends Condition {
		@JsonProperty("NumericEquals")
		private Number value;
		public NumericEquals(String variable, Number value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Number getValue() {
			return value;
		}
	}
	public static class NumericLessThan extends Condition {
		@JsonProperty("NumericLessThan")
		private Number value;
		public NumericLessThan(String variable, Number value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Number getValue() {
			return value;
		}
	}
	public static class NumericGreaterThan extends Condition {
		@JsonProperty("NumericGreaterThan")
		private Number value;
		public NumericGreaterThan(String variable, Number value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Number getValue() {
			return value;
		}
	}
	public static class NumericLessThanEquals extends Condition {
		@JsonProperty("NumericLessThanEquals")
		private Number value;
		public NumericLessThanEquals(String variable, Number value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Number getValue() {
			return value;
		}
	}
	public static class NumericGreaterThanEquals extends Condition {
		@JsonProperty("NumericGreaterThanEquals")
		private Number value;
		public NumericGreaterThanEquals(String variable, Number value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Number getValue() {
			return value;
		}
	}
	
	public static class StringEquals extends Condition {
		@JsonProperty("StringEquals")
		private String value;
		public StringEquals(String variable, String value) {
			super(variable);
			this.value = value;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	public static class StringLessThan extends Condition {
		@JsonProperty("StringLessThan")
		private String value;
		public StringLessThan(String variable, String value) {
			super(variable);
			this.value = value;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	public static class StringGreaterThan extends Condition {
		@JsonProperty("StringGreaterThan")
		private String value;
		public StringGreaterThan(String variable, String value) {
			super(variable);
			this.value = value;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	public static class StringLessThanEquals extends Condition {
		@JsonProperty("StringLessThanEquals")
		private String value;
		public StringLessThanEquals(String variable, String value) {
			super(variable);
			this.value = value;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	public static class StringGreaterThanEquals extends Condition {
		@JsonProperty("StringGreaterThanEquals")
		private String value;
		public StringGreaterThanEquals(String variable, String value) {
			super(variable);
			this.value = value;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	
	public static class TimestampEquals extends Condition {
		@JsonProperty("TimestampEquals")
		private Date value;
		public TimestampEquals(String variable, Date value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Date getValue() {
			return value;
		}
	}
	public static class TimestampLessThan extends Condition {
		@JsonProperty("TimestampLessThan")
		private Date value;
		public TimestampLessThan(String variable, Date value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Date getValue() {
			return value;
		}
	}
	public static class TimestampGreaterThan extends Condition {
		@JsonProperty("TimestampGreaterThan")
		private Date value;
		public TimestampGreaterThan(String variable, Date value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Date getValue() {
			return value;
		}
	}
	public static class TimestampLessThanEquals extends Condition {
		@JsonProperty("TimestampLessThanEquals")
		private Date value;
		public TimestampLessThanEquals(String variable, Date value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Date getValue() {
			return value;
		}
	}
	public static class TimestampGreaterThanEquals extends Condition {
		@JsonProperty("TimestampGreaterThanEquals")
		private Date value;
		public TimestampGreaterThanEquals(String variable, Date value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Date getValue() {
			return value;
		}
	}
	
	//LOGICAL OPERATORS
	public static class And extends RuleSpec {
		@JsonProperty("And")
		private List<Condition> conditions;
		public And(String next, List<Condition> conditions) {
			super(next);
			this.conditions = conditions;
		}
		public List<Condition> getConditions() {
			return conditions;
		}
	}
	public static class Or extends RuleSpec {
		@JsonProperty("Or")
		private List<Condition> conditions;
		public Or(String next, List<Condition> conditions) {
			super(next);
			this.conditions = conditions;
		}
		public List<Condition> getConditions() {
			return conditions;
		}
	}
	public static class Not extends RuleSpec {
		@JsonProperty("Not")
		private Condition condition;
		public Not(String next, Condition condition) {
			super(next);
			this.condition = condition;
		}
		public Condition getCondition() {
			return condition;
		}
	}
	
	//CUSTOM RULE BUILDER
	static class RuleBuilder {
		private String next;
		private String operator;
		private Object value;
		
		@JsonProperty("Next")
		public RuleBuilder withNext(String next) {
			this.next = next;
			return this;
		}
		
		@JsonProperty("Not")
		public RuleBuilder withNot(Condition condition) {
			this.operator = "Not";
			this.value	  = condition;
			return this;
		}
		
		//OTHERS
		@JsonAnySetter
		public RuleBuilder withOperator(String operator, List<Condition> conditions) {
			this.operator = operator;
			this.value	  = conditions;
			return this;
		}
		//
		public RuleSpec build() {
			switch(operator) {
				case "Not":
					return new Not(next, (Condition)value);
				case "Or":
					return new Or(next, Objects.cast(value));
				case "And":
					return new And(next, Objects.cast(value));
			}
			throw new IllegalArgumentException("Unknown rule with operator: [" + operator + "], value: [" + value + "]");
		}
	}
	
	//CUSTOM CONDITION BUILDER
	static class ConditionBuilder {
		private String variable;
		private String operator;
		private Object value;
		
		@JsonProperty("Variable")
		public ConditionBuilder withVariable(String variable) {
			this.variable = variable;
			return this;
		}
		
		//OTHERS
		@JsonAnySetter
		public ConditionBuilder withOperator(String operator, Object value) {
			this.operator = operator;
			this.value	  = value;
			return this;
		}
		public Condition build() {
			switch(operator) {
				case "BooleanEquals":
					return new BooleanEquals(variable, Objects.cast(value));
					
				case "NumericEquals":
					return new NumericEquals(variable, Objects.cast(value));
				case "NumericLessThan":
					return new NumericLessThan(variable, Objects.cast(value));
				case "NumericGreaterThan":
					return new NumericGreaterThan(variable, Objects.cast(value));
				case "NumericLessThanEquals":
					return new NumericLessThanEquals(variable, Objects.cast(value));
				case "NumericGreaterThanEquals":
					return new NumericGreaterThanEquals(variable, Objects.cast(value));
				
				case "StringEquals":
					return new StringEquals(variable, Objects.cast(value));
				case "StringLessThan":
					return new StringLessThan(variable, Objects.cast(value));
				case "StringGreaterThan":
					return new StringGreaterThan(variable, Objects.cast(value));
				case "StringLessThanEquals":
					return new StringLessThanEquals(variable, Objects.cast(value));
				case "StringGreaterThanEquals":
					return new StringGreaterThanEquals(variable, Objects.cast(value));
				
				case "TimestampEquals":
					return new TimestampEquals(variable, Objects.cast(value));
				case "TimestampLessThan":
					return new TimestampLessThan(variable, Objects.cast(value));
				case "TimestampGreaterThan":
					return new NumericGreaterThan(variable, Objects.cast(value));
				case "TimestampLessThanEquals":
					return new TimestampLessThanEquals(variable, Objects.cast(value));
				case "TimestampGreaterThanEquals":
					return new TimestampGreaterThanEquals(variable, Objects.cast(value));
			}
			throw new IllegalArgumentException("Unknown condition with operator: [" + operator + "], value: [" + value + "]");
		}
	}
}
