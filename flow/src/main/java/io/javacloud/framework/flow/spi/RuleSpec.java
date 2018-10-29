package io.javacloud.framework.flow.spi;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
@JsonDeserialize(builder = RuleSpec.RuleBuilder.class)
public abstract class RuleSpec implements StateTransition.Success {
	@JsonProperty("Next")
	private String next;
	protected RuleSpec(String next) {
		this.next = next;
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
	
	public static class StringEquals extends Condition {
		@JsonProperty("StringEquals")
		private String value;
		public StringEquals(String variable, String value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
			return value;
		}
	}
	
	public static class BooleanEquals extends Condition {
		@JsonProperty("BooleanEquals")
		private Boolean value;
		public BooleanEquals(String variable, boolean value) {
			super(variable);
			this.value = value;
		}
		@Override
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
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
		public Object getValue() {
			return value;
		}
	}
	
	//LOGICAL OPERATOR
	public static class And extends RuleSpec {
		@JsonProperty("And")
		private List<Condition> conditions;
		public And(String next, List<Condition> conditions) {
			super(next);
			this.conditions = conditions;
		}
	}
	public static class Or extends RuleSpec {
		@JsonProperty("Or")
		private List<Condition> conditions;
		public Or(String next, List<Condition> conditions) {
			super(next);
			this.conditions = conditions;
		}
	}
	public static class Not extends RuleSpec {
		@JsonProperty("Not")
		private Condition condition;
		public Not(String next, Condition condition) {
			super(next);
			this.condition = condition;
		}
	}
	
	//CUSTOM RULE BUILDER
	static class RuleBuilder {
		private String next;
		private Class<? extends RuleSpec> type;
		private Object value;
		
		@JsonProperty("Next")
		public RuleBuilder withNext(String next) {
			this.next = next;
			return this;
		}
		
		@JsonProperty("And")
		public RuleBuilder withAnd(List<Condition> value) {
			this.type = And.class;
			this.value = value;
			return this;
		}
		@JsonProperty("Or")
		public RuleBuilder withNot(List<Condition> value) {
			this.type = Or.class;
			this.value = value;
			return this;
		}
		@JsonProperty("Not")
		public RuleBuilder withNot(Condition value) {
			this.type = Not.class;
			this.value = value;
			return this;
		}
		//
		public RuleSpec build() {
			if(type == Not.class) {
				return new Not(next, (Condition)value);
			}
			if(type == Or.class) {
				return new Or(next, Objects.cast(value));
			}
			if(type == And.class) {
				return new And(next, Objects.cast(value));
			}
			//UNKNOWN
			return null;
		}
	}
	
	//CUSTOM CONDITION BUILDER
	static class ConditionBuilder {
		private String variable;
		private Class<? extends Condition> type;
		private Object value;
		
		@JsonProperty("Variable")
		public ConditionBuilder withVariable(String variable) {
			this.variable = variable;
			return this;
		}
		@JsonProperty("StringEquals")
		public ConditionBuilder withStringEquals(String value) {
			this.type = StringEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("StringLessThan")
		public ConditionBuilder withStringLessThan(String value) {
			this.type = StringLessThan.class;
			this.value = value;
			return this;
		}
		@JsonProperty("StringGreaterThan")
		public ConditionBuilder withStringGreaterThan(String value) {
			this.type = StringGreaterThan.class;
			this.value = value;
			return this;
		}
		@JsonProperty("StringLessThanEquals")
		public ConditionBuilder withStringLessThanEquals(String value) {
			this.type = StringLessThanEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("StringGreaterThanEquals")
		public ConditionBuilder withStringGreaterThanEquals(String value) {
			this.type = StringGreaterThanEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("NumericEquals")
		public ConditionBuilder withNumericEquals(Number value) {
			this.type = NumericEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("NumericLessThan")
		public ConditionBuilder withNumericLessThan(Number value) {
			this.type = NumericLessThan.class;
			this.value = value;
			return this;
		}
		@JsonProperty("NumericGreaterThan")
		public ConditionBuilder withNumericGreaterThan(Number value) {
			this.type = NumericGreaterThan.class;
			this.value = value;
			return this;
		}
		@JsonProperty("NumericLessThanEquals")
		public ConditionBuilder withNumericLessThanEquals(Number value) {
			this.type = NumericLessThanEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("NumericGreaterThanEquals")
		public ConditionBuilder withNumericGreaterThanEquals(Number value) {
			this.type = NumericGreaterThanEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("BooleanEquals")
		public ConditionBuilder withBooleanEquals(boolean value) {
			this.type = BooleanEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("TimestampEquals")
		public ConditionBuilder withTimestampEquals(Date value) {
			this.type = TimestampEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("TimestampLessThan")
		public ConditionBuilder withTimestampLessThan(Date value) {
			this.type = TimestampLessThan.class;
			this.value = value;
			return this;
		}
		@JsonProperty("TimestampGreaterThan")
		public ConditionBuilder withTimestampGreaterThan(Date value) {
			this.type = TimestampGreaterThan.class;
			this.value = value;
			return this;
		}
		@JsonProperty("TimestampLessThanEquals")
		public ConditionBuilder withTimestampLessThanEquals(Date value) {
			this.type = TimestampLessThanEquals.class;
			this.value = value;
			return this;
		}
		@JsonProperty("TimestampGreaterThanEquals")
		public ConditionBuilder withTimestampGreaterThanEquals(Date value) {
			this.type = TimestampGreaterThanEquals.class;
			this.value = value;
			return this;
		}
		
		public Condition build() {
			if(type == StringEquals.class) {
				return new StringEquals(variable, Objects.cast(value));
			}
			if(type == StringLessThan.class) {
				return new StringLessThan(variable, Objects.cast(value));
			}
			if(type == StringGreaterThan.class) {
				return new StringGreaterThan(variable, Objects.cast(value));
			}
			if(type == StringLessThanEquals.class) {
				return new StringLessThanEquals(variable, Objects.cast(value));
			}
			if(type == StringGreaterThanEquals.class) {
				return new StringGreaterThanEquals(variable, Objects.cast(value));
			}
			if(type == NumericEquals.class) {
				return new NumericEquals(variable, Objects.cast(value));
			}
			if(type == NumericLessThan.class) {
				return new NumericLessThan(variable, Objects.cast(value));
			}
			if(type == NumericGreaterThan.class) {
				return new NumericGreaterThan(variable, Objects.cast(value));
			}
			if(type == NumericLessThanEquals.class) {
				return new NumericLessThanEquals(variable, Objects.cast(value));
			}
			if(type == NumericGreaterThanEquals.class) {
				return new NumericGreaterThanEquals(variable, Objects.cast(value));
			}
			if(type == BooleanEquals.class) {
				return new BooleanEquals(variable, Objects.cast(value));
			}
			if(type == TimestampEquals.class) {
				return new TimestampEquals(variable, Objects.cast(value));
			}
			if(type == TimestampLessThan.class) {
				return new TimestampLessThan(variable, Objects.cast(value));
			}
			if(type == TimestampGreaterThan.class) {
				return new TimestampGreaterThan(variable, Objects.cast(value));
			}
			if(type == TimestampLessThanEquals.class) {
				return new TimestampLessThanEquals(variable, Objects.cast(value));
			}
			if(type == TimestampGreaterThanEquals.class) {
				return new TimestampGreaterThanEquals(variable, Objects.cast(value));
			}
			//UNKNOWN
			return null;
		}
	}
}
