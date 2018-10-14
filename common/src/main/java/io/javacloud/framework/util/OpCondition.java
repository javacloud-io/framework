package io.javacloud.framework.util;


/**
 * Very simple data condition, represent a condition with its value associated.
 * 
 * @author tobi
 */
public abstract class OpCondition<T> implements java.util.function.Predicate<T> {
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
	private Op	op;		//range key comparator
	protected T[] values;//which range keys
	/**
	 * 
	 * @param operator
	 * @param values
	 */
	@SafeVarargs
	protected OpCondition(Op op, T... values) {
		this.op = op;
		this.values = values;
	}
	
	/**
	 * DUMP NOT NULL B
	 */
	protected OpCondition() {
		this(Op.NULL);
	}
	
	/**
	 * return the operator
	 * @return
	 */
	public Op getOp() {
		return op;
	}
	
	/**
	 * return multiple values
	 * 
	 * @return
	 */
	public T[] getValues() {
		return Objects.cast(values);
	}
	
	/**
	 * return single value
	 * @return
	 */
	public T getValue() {
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
			.append(Converters.toString(",", values))
			.append("]");
		} else if(op != Op.NULL && op != Op.NOT_NULL) {
			sb.append(" ").append((Object)getValue());
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> OpCondition<T> EQ(T value) {
		return new OpCondition<T>(Op.EQ, value) {
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
	public static final <T> OpCondition<T> NE(T value) {
		return new OpCondition<T>(Op.NE, value) {
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
	public static final <T> OpCondition<T> LT(T value) {
		return new OpCondition<T>(Op.LT, value) {
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
	public static final <T> OpCondition<T> LE(T value) {
		return new OpCondition<T>(Op.LE, value) {
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
	public static final <T> OpCondition<T> GT(T value) {
		return new OpCondition<T>(Op.GT, value) {
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
	public static final <T> OpCondition<T> GE(T value) {
		return new OpCondition<T>(Op.GE, value) {
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
	public static final <T> OpCondition<T> NULL() {
		return new OpCondition<T>(Op.NULL) {
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
	public static final <T> OpCondition<T> NOT_NULL() {
		return new OpCondition<T>(Op.NOT_NULL) {
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
	public static final <T> OpCondition<T> BETWEEN(T value1, T value2) {
		return new OpCondition<T>(Op.BETWEEN, value1, value2) {
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
	public static final <T> OpCondition<T> STARTS_WITH(T value) {
		return new OpCondition<T>(Op.STARTS_WITH, value) {
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
	public static final <T> OpCondition<T> IN(T... values) {
		return new OpCondition<T>(Op.IN, values) {
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
	public static final <T> OpCondition<T> NOP() {
		return null;
	}
}
