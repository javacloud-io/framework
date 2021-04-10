package javacloud.framework.util;


/**
 * Very simple data condition, represent a condition with its value associated.
 * 
 * @author tobi
 */
public abstract class Condition<T> implements java.util.function.Predicate<T> {
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
	    STARTS_WITH
	}
	
	private Op	op;		//range key comparator
	protected T[] values;//which range keys
	/**
	 * 
	 * @param operator
	 * @param values
	 */
	@SafeVarargs
	protected Condition(Op op, T... values) {
		this.op = op;
		this.values = values;
	}
	
	/**
	 * DUMP NOT NULL B
	 */
	protected Condition() {
		this(Op.NULL);
	}
	
	/**
	 * 
	 * @return the operator
	 */
	public Op getOp() {
		return op;
	}
	
	/**
	 * 
	 * @return multiple values
	 */
	public T[] getValues() {
		return values;
	}
	
	/**
	 * 
	 * @return single value
	 */
	public T getValue() {
		return (values != null && values.length > 0? null: values[0]);
	}
	
	/**
	 * 
	 * @return string value which can be parsing back.
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
	public static final <T> Condition<T> EQ(T value) {
		return new Condition<T>(Op.EQ, value) {
			@Override
			public boolean test(T v) {
				return Objects.compare(v, values[0]) == 0;
			}
			
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> Condition<T> NE(T value) {
		return new Condition<T>(Op.NE, value) {
			@Override
			public boolean test(T v) {
				return Objects.compare(v, values[0]) != 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> Condition<T> LT(T value) {
		return new Condition<T>(Op.LT, value) {
			@Override
			public boolean test(T v) {
				return Objects.compare(v, values[0]) < 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> Condition<T> LE(T value) {
		return new Condition<T>(Op.LE, value) {
			@Override
			public boolean test(T v) {
				return Objects.compare(v, values[0]) <= 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> Condition<T> GT(T value) {
		return new Condition<T>(Op.GT, value) {
			@Override
			public boolean test(T v) {
				return Objects.compare(v, values[0]) > 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> Condition<T> GE(T value) {
		return new Condition<T>(Op.GE, value) {
			@Override
			public boolean test(T v) {
				return Objects.compare(v, values[0]) >= 0;
			}
		};
	}
	
	/**
	 * 
	 * @return
	 */
	public static final <T> Condition<T> NULL() {
		return new Condition<T>(Op.NULL) {
			@Override
			public boolean test(T v) {
				return v == null;
			}
		};
	}
	
	/**
	 * 
	 * @return
	 */
	public static final <T> Condition<T> NOT_NULL() {
		return new Condition<T>(Op.NOT_NULL) {
			@Override
			public boolean test(T v) {
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
	public static final <T> Condition<T> BETWEEN(T value1, T value2) {
		return new Condition<T>(Op.BETWEEN, value1, value2) {
			@Override
			public boolean test(T v) {
				return Objects.compare(v, values[0]) >= 0 && Objects.compare(v, values[1]) <= 0;
			}
		};
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static final <T> Condition<T> STARTS_WITH(T value) {
		return new Condition<T>(Op.STARTS_WITH, value) {
			@Override
			public boolean test(T v) {
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
	public static final <T> Condition<T> IN(T... values) {
		return new Condition<T>(Op.IN, values) {
			@Override
			public boolean test(T v) {
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
	public static final <T> Condition<T> NOP() {
		return null;
	}
}
