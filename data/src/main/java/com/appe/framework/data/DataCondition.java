package com.appe.framework.data;

import com.appe.framework.util.Objects;

/**
 * Very simple data condition, represent a condition with its value associated.
 * 
 * @author tobi
 */
public abstract class DataCondition {
	public enum Op {
		EQ,
		NE,
	    LE,
	    LT,
	    GE,
	    GT,
	    IN,
	    BETWEEN,
	    NULL,
	    NOT_NULL,
	    STARTS_WITH;
	}
	private Op	op;	//range key comparator
	protected Object[] values;//which range keys
	/**
	 * 
	 * @param operator
	 * @param values
	 */
	protected DataCondition(Op op, Object... values) {
		this.op = op;
		this.values = values;
	}
	
	/**
	 * DUMP OP FOR NOW
	 */
	protected DataCondition() {
		this(Op.NULL);
	}
	
	/**
	 * return the operator
	 * @return
	 */
	public DataCondition.Op getOp() {
		return op;
	}
	
	/**
	 * Multiple value
	 * @return
	 */
	public <T> T[] getValues() {
		return Objects.cast(values);
	}
	
	/**
	 * In case of single value
	 * @return
	 */
	public <T> T getValue() {
		return Objects.cast(values != null && values.length > 0? null: values[0]);
	}
	
	/**
	 * return string value which can be parsing back.
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(op.toString());
		if(op == Op.IN || op == Op.BETWEEN) {
			sb.append(" [")
			.append(Objects.toString(",", values))
			.append("]");
		} else if(op != Op.NULL && op != Op.NOT_NULL) {
			sb.append(" ").append((Object)getValue());
		}
		return sb.toString();
	}

	/**
	 * Basic condition test for general purpose.
	 * @param o
	 * @return
	 */
	public abstract boolean test(Object v);
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> DataCondition EQ(T value) {
		return new DataCondition(Op.EQ, value) {
			@Override
			public boolean test(Object v) {
				return Objects.compare(v, values[0]) == 0;
			}
			
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> DataCondition NE(T value) {
		return new DataCondition(Op.NE, value) {
			@Override
			public boolean test(Object v) {
				return Objects.compare(v, values[0]) != 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> DataCondition LT(T value) {
		return new DataCondition(Op.LT, value) {
			@Override
			public boolean test(Object v) {
				return Objects.compare(v, values[0]) < 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> DataCondition LE(T value) {
		return new DataCondition(Op.LE, value) {
			@Override
			public boolean test(Object v) {
				return Objects.compare(v, values[0]) <= 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> DataCondition GT(T value) {
		return new DataCondition(Op.GT, value) {
			@Override
			public boolean test(Object v) {
				return Objects.compare(v, values[0]) > 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> DataCondition GE(T value) {
		return new DataCondition(Op.GE, value) {
			@Override
			public boolean test(Object v) {
				return Objects.compare(v, values[0]) >= 0;
			}
		};
	}
	
	/**
	 * 
	 * @return
	 */
	public static final <T> DataCondition NULL() {
		return new DataCondition(Op.NULL) {
			@Override
			public boolean test(Object v) {
				return v == null;
			}
		};
	}
	
	/**
	 * 
	 * @return
	 */
	public static final <T> DataCondition NOT_NULL() {
		return new DataCondition(Op.NOT_NULL) {
			@Override
			public boolean test(Object v) {
				return v != null;
			}
		};
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static final <T> DataCondition BETWEEN(T value1, T value2) {
		return new DataCondition(Op.BETWEEN, value1, value2) {
			@Override
			public boolean test(Object v) {
				return Objects.compare(v, values[0]) >= 0 && Objects.compare(v, values[1]) <= 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> DataCondition STARTS_WITH(T value) {
		return new DataCondition(Op.STARTS_WITH, value) {
			@Override
			public boolean test(Object v) {
				return v.toString().startsWith(values[0].toString());
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	@SafeVarargs
	public static final <T> DataCondition IN(T... values) {
		return new DataCondition(Op.IN, values) {
			@Override
			public boolean test(Object v) {
				for(Object t: values) {
					if(Objects.compare(v, t) == 0) {
						return true;
					}
				}
				return false;
			}
		};
	}
	
	/**
	 * NOT AN OPERATOR JUST FOR NULL CONVERSION.
	 * @return
	 */
	public static final <T> DataCondition NOP() {
		return null;
	}
}
