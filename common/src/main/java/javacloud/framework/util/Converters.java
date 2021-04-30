package javacloud.framework.util;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Converting from something to something else. String to bytes[] will automatically assuming base64 encoded!!!
 * 
 * @author tobi
 */
public final class Converters {
	private Converters() {
	}
	
	//ANYTHING
	public static final Function<Object, Object> OBJECT = new Function<Object, Object>() {
		@Override
		public Object apply(Object value) {
			return value;
		}
	};
	
	//BOOLEAN | NUMBER | STRING
	public static final Function<Object, Boolean> BOOLEAN = new Function<Object, Boolean>() {
		@Override
		public Boolean apply(Object value) {
			if (value instanceof Boolean) {
				return (Boolean)value;
			} else if (value instanceof Number) {
				return ((Number)value).intValue() > 0;
			}
			return (value != null ? Boolean.valueOf((String)value) : null);
		}
	};
	
	//BYTE | NUMBER | STRING
	public static final Function<Object, Byte> BYTE = new Function<Object, Byte>() {
		@Override
		public Byte apply(Object value) {
			if (value instanceof Byte) {
				return (Byte)value;
			} else if (value instanceof Number) {
				return ((Number)value).byteValue();
			}
			return (value != null ? Byte.valueOf((String)value) : null);
		}
	};
	
	//CHAR | NUMBER | STRING
	public static final Function<Object, Character> CHARACTER = new Function<Object, Character>() {
		@Override
		public Character apply(Object value) {
			if (value instanceof Character) {
				return (Character)value;
			} else if (value instanceof Number) {
				return (char)((Number)value).intValue();
			}
			return (value != null ? (((String)value).length() == 0? '\0' : ((String)value).charAt(0)) : null);
		}
	};
	
	//INTEGER  | NUMBER | STRING
	public static final Function<Object, Integer> INTEGER = new Function<Object, Integer>() {
		@Override
		public Integer apply(Object value) {
			if (value instanceof Integer) {
				return (Integer)value;
			} else if (value instanceof Number) {
				return ((Number)value).intValue();
			}
			return (value != null ? Integer.valueOf((String)value) : null);
		}
	};
	
	//SHORT | NUMBER | STRING
	public static final Function<Object, Short> SHORT = new Function<Object, Short>() {
		@Override
		public Short apply(Object value) {
			if (value instanceof Short) {
				return (Short)value;
			} else if (value instanceof Number) {
				return ((Number)value).shortValue();
			}
			return (value != null ? Short.valueOf((String)value) : null);
		}
	};
	
	//LONG | NUMBER | DATE | STRING
	public static final Function<Object, Long> LONG = new Function<Object, Long>() {
		@Override
		public Long apply(Object value) {
			if (value instanceof Long) {
				return (Long)value;
			} else if (value instanceof Number) {
				return ((Number)value).longValue();
			} else if (value instanceof java.util.Date) {
				return ((java.util.Date)value).getTime();
			}
			return (value != null ? Long.valueOf((String)value) : null);
		}
	};
	
	//FLOAT | NUMBER | STRING
	public static final Function<Object, Float> FLOAT = new Function<Object, Float>() {
		@Override
		public Float apply(Object value) {
			if (value instanceof Float) {
				return (Float)value;
			} else if (value instanceof Number) {
				return ((Number)value).floatValue();
			}
			return (value != null ? Float.valueOf((String)value) : null);
		}
	};
	
	//DOUBLE | NUMBER | STRING
	public static final Function<Object, Double> DOUBLE = new Function<Object, Double>() {
		@Override
		public Double apply(Object value) {
			if (value instanceof Double) {
				return (Double)value;
			} else if (value instanceof Number) {
				return ((Number)value).doubleValue();
			}
			return (value != null ? Double.valueOf((String)value) : null);
		}
	};
	
	//BYTES | BYTEB | BASE64 ENCODED
	public static final Function<Object, ByteBuffer> BYTEB = new Function<Object, ByteBuffer>() {
		@Override
		public ByteBuffer apply(Object value) {
			if (value instanceof ByteBuffer) {
				return (ByteBuffer)value;
			} else if (value instanceof byte[]) {
				return ByteBuffer.wrap((byte[])value);
			}
			
			//ASSUMING BASE64 STRING ENCODED?
			return (value != null ? ByteBuffer.wrap(Codecs.Base64Decoder.apply((String)value, false)) : null);
		}
	};
	
	//BYTES | BYTEB | BASE64 ENCODED
	public static final Function<Object, byte[]> BYTES = new Function<Object, byte[]>() {
		@Override
		public byte[] apply(Object value) {
			if (value instanceof ByteBuffer) {
				ByteBuffer buf = (ByteBuffer)value;
				byte[] dst = new byte[buf.remaining()];
				buf.get(dst);
				return dst;
			} else if (value instanceof byte[]) {
				return (byte[])value;
			}
			
			//ASSUMING BASE64 STRING ENCODED?
			return (value != null ? Codecs.Base64Decoder.apply((String)value, false) : null);
		}
	};
	
	//DATE
	public static final Function<Object, Date> DATE = new Function<Object, Date>() {
		@Override
		public Date apply(Object value) {
			if (value instanceof Date) {
				return	(Date)value;
			} else if (value instanceof Number) {
				return new Date(((Number)value).longValue());
			}
			//USING ISO DATE
			try {
				return (value != null? DateFormats.getUTC(DateFormats.ISO8601_S3).parse((String)value) : null);
			}catch(ParseException ex) {
				throw InternalException.of(ex);
			}
		}
	};
	
	//STRING
	public static final Function<Object, String> STRING = new Function<Object, String>() {
		@Override
		public String apply(Object value) {
			if (value instanceof String) {
				return	(String)value;
			} else if (value instanceof Date) {
				return DateFormats.getUTC(DateFormats.ISO8601_S3).format((Date)value);
			} else if (value instanceof byte[]) {
				return Codecs.Base64Encoder.apply((byte[])value, false);
			}
			return (value != null? value.toString() : null);
		}
	};
	
	//PRIMITIVES MAP
	public static final Map<Class<?>, Function<Object, ?>> TYPES = Objects.asMap(
			boolean.class, 		BOOLEAN,
			Boolean.class, 		BOOLEAN,
			byte.class,    		BYTE,
			Byte.class, 		BYTE,
			char.class,			CHARACTER,
			Character.class,	CHARACTER,
			short.class,		SHORT,
			Short.class,		SHORT,
			int.class,			INTEGER,
			Integer.class,		INTEGER,
			long.class,			LONG,
			Long.class,			LONG,
			float.class,		FLOAT,
			Float.class,		FLOAT,
			double.class,		DOUBLE,
			Double.class,		DOUBLE,
			String.class,		STRING,
			Date.class,			DATE,
			byte[].class,		BYTES,
			ByteBuffer.class,	BYTEB
	);
	
	/**
	 * Convert an object to STRING
	 * 
	 * @param value
	 * @return
	 */
	public static String toString(Object value) {
		if (value instanceof Object[]) {
			return toString(",", (Object[])value);
		} else if (value instanceof Collection) {
			return toString(",", ((Collection<?>)value).toArray());
		}
		return STRING.apply(value);
	}
	
	/**
	 * Assuming the coming list value doesn't have comma character.
	 * 
	 * @param name
	 * @param list
	 */
	public static String toString(String sep, Object... list) {
		//Don't write anything
		if(list == null || list.length == 0) {
			return null;
		}
		
		//Build the list with comma separated.
		StringBuilder buf = new StringBuilder();
		buf.append(toString(list[0]));
		for(int i = 1; i < list.length; i ++) {
			buf.append(sep)
			   .append(toString(list[i]));
		}
		return buf.toString();
	}
	
	/**
	 * Convert the string value back to ARRAY.
	 * 
	 * @param value
	 * @param sep
	 * @param trim
	 * @return
	 */
	public static String[] toArray(String value, String sep, boolean trim) {
		//MAKE SURE RETURN EMPTY LIST
		if (Objects.isEmpty(value)) {
			return new String[0];
		}
		
		//SPLIT IT UP
		String[] list = value.split(sep);
		if (trim) {
			for(int i = 0; i < list.length; i ++) {
				list[i] = list[i].trim();
			}
		}
		return list;
	}
	
	/**
	 * Convert primitive string to Object
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String value, Class<?> type) {
		//STRING
		if (type == String.class || type == Object.class) {
			return (T)value;
		}
		
		//ENUM OBJECT
		if (type.isEnum()) {
			return	(T)Enum.valueOf(type.asSubclass(Enum.class), value);
		}
		
		//ARRAY OF STRING/OBJECT
		if (type == String[].class) {
			return Objects.cast(toArray(value, ",", true));
		} else if (type.isArray()) {
			ArrayList<Object> list = new ArrayList<>();
			for (String s: toArray(value, ",", true)) {
				list.add(toObject(s, type.getComponentType()));
			}
			Object arr = Array.newInstance(type.getComponentType(), list.size());
			for (int i = 0; i < list.size(); i ++) {
				Array.set(arr, i, list.get(i));
			}
			return (T)arr;
		} else if (type.isAssignableFrom(ArrayList.class)) {
			ArrayList<Object> list = new ArrayList<>();
			for(String s: toArray(value, ",", true)) {
				list.add(s);
			}
			return (T)list;
		}
		
		//PRIMITIVE OBJECT
		Function<Object, ?> c = TYPES.get(type);
		if (c != null) {
			return (T)c.apply(value);
		}
		
		//UNKNOW TYPE => RETURN AS IS!!!
		return (T)value;
	}
}
