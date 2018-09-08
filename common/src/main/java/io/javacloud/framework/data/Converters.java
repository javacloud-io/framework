package io.javacloud.framework.data;

import io.javacloud.framework.util.Codecs;
import io.javacloud.framework.util.DateFormats;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.UncheckedException;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Converting from something to something else. String to bytes[] will automatically assuming base64 encoded!!!
 * 
 * @author tobi
 */
public final class Converters {
	private Converters() {
	}
	
	//ANYTHING
	public static final Converter<Object> OBJECT = new Converter<Object>() {
		@Override
		public Object to(Object value) {
			return value;
		}
	};
	
	//BOOLEAN | NUMBER | STRING
	public static final Converter<Boolean> BOOLEAN = new Converter<Boolean>() {
		@Override
		public Boolean to(Object value) {
			if(value instanceof Boolean) {
				return (Boolean)value;
			} else if(value instanceof Number) {
				return ((Number)value).intValue() > 0;
			}
			return (value != null ? Boolean.valueOf((String)value) : null);
		}
	};
	//BYTE | NUMBER | STRING
	public static final Converter<Byte> BYTE = new Converter<Byte>() {
		@Override
		public Byte to(Object value) {
			if(value instanceof Byte) {
				return (Byte)value;
			} else if(value instanceof Number) {
				return ((Number)value).byteValue();
			}
			return (value != null ? Byte.valueOf((String)value) : null);
		}
	};
	//CHAR | NUMBER | STRING
	public static final Converter<Character> CHARACTER = new Converter<Character>() {
		@Override
		public Character to(Object value) {
			if(value instanceof Character) {
				return (Character)value;
			} else if(value instanceof Number) {
				return (char)((Number)value).intValue();
			}
			return (value != null ? (((String)value).length() == 0? '\0' : ((String)value).charAt(0)) : null);
		}
	};
	//INTEGER  | NUMBER | STRING
	public static final Converter<Integer> INTEGER = new Converter<Integer>() {
		@Override
		public Integer to(Object value) {
			if(value instanceof Integer) {
				return (Integer)value;
			} else if(value instanceof Number) {
				return ((Number)value).intValue();
			}
			return (value != null ? Integer.valueOf((String)value) : null);
		}
	};
	//SHORT | NUMBER | STRING
	public static final Converter<Short> SHORT = new Converter<Short>() {
		@Override
		public Short to(Object value) {
			if(value instanceof Short) {
				return (Short)value;
			} else if(value instanceof Number) {
				return ((Number)value).shortValue();
			}
			return (value != null ? Short.valueOf((String)value) : null);
		}
	};
	//LONG | NUMBER | DATE | STRING
	public static final Converter<Long> LONG = new Converter<Long>() {
		@Override
		public Long to(Object value) {
			if(value instanceof Long) {
				return (Long)value;
			} else if(value instanceof Number) {
				return ((Number)value).longValue();
			} else if(value instanceof java.util.Date) {
				return ((java.util.Date)value).getTime();
			}
			return (value != null ? Long.valueOf((String)value) : null);
		}
	};
	//FLOAT | NUMBER | STRING
	public static final Converter<Float> FLOAT = new Converter<Float>() {
		@Override
		public Float to(Object value) {
			if(value instanceof Float) {
				return (Float)value;
			} else if(value instanceof Number) {
				return ((Number)value).floatValue();
			}
			return (value != null ? Float.valueOf((String)value) : null);
		}
	};
	//DOUBLE | NUMBER | STRING
	public static final Converter<Double> DOUBLE = new Converter<Double>() {
		@Override
		public Double to(Object value) {
			if(value instanceof Double) {
				return (Double)value;
			} else if(value instanceof Number) {
				return ((Number)value).doubleValue();
			}
			return (value != null ? Double.valueOf((String)value) : null);
		}
	};
	
	//BYTES | BYTEB | BASE64 ENCODED
	public static final Converter<ByteBuffer> BYTEB = new Converter<ByteBuffer>() {
		@Override
		public ByteBuffer to(Object value) {
			if(value instanceof ByteBuffer) {
				return (ByteBuffer)value;
			} else if(value instanceof byte[]) {
				return ByteBuffer.wrap((byte[])value);
			}
			
			//ASSUMING BASE64 STRING ENCODED?
			return (value != null ? ByteBuffer.wrap(Codecs.decodeBase64((String)value, false)) : null);
		}
	};
	//BYTES | BYTEB | BASE64 ENCODED
	public static final Converter<byte[]> BYTES = new Converter<byte[]>() {
		@Override
		public byte[] to(Object value) {
			if(value instanceof ByteBuffer) {
				ByteBuffer buf = (ByteBuffer)value;
				byte[] dst = new byte[buf.remaining()];
				buf.get(dst);
				return dst;
			} else if(value instanceof byte[]) {
				return (byte[])value;
			}
			
			//ASSUMING BASE64 STRING ENCODED?
			return (value != null ? Codecs.decodeBase64((String)value, false) : null);
		}
	};
	//DATE
	public static final Converter<Date> DATE = new Converter<Date>() {
		@Override
		public Date to(Object value) {
			if(value instanceof Date) {
				return	(Date)value;
			} else if(value instanceof Number) {
				return new Date(((Number)value).longValue());
			}
			//USING ISO DATE
			try {
				return (value != null? DateFormats.getUTC(DateFormats.ISO8601_S3).parse((String)value) : null);
			}catch(ParseException ex) {
				throw UncheckedException.wrap(ex);
			}
		}
	};
	//STRING
	public static final Converter<String> STRING = new Converter<String>() {
		@Override
		public String to(Object value) {
			if(value instanceof String) {
				return	(String)value;
			} else if(value instanceof Date) {
				return DateFormats.getUTC(DateFormats.ISO8601_S3).format((Date)value);
			} else if(value instanceof byte[]) {
				return Codecs.encodeBase64((byte[])value, false);
			} else if(value instanceof Object[]) {
				return	Arrays.asList((Object[])value).toString();
			}
			return (value != null? value.toString() : null);
		}
	};
	
	//PRIMITIVES MAP
	public static final Map<Class<?>, Converter<?>> TYPES = Objects.asMap(
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
		if(value instanceof Object[]) {
			return toString(",", (Object[])value);
		}
		return STRING.to(value);
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
		buf.append(STRING.to(list[0]));
		for(int i = 1; i < list.length; i ++) {
			buf.append(sep)
			   .append(STRING.to(list[i]));
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
		if(value == null) {
			return new String[0];
		}
		
		//SPLIT IT UP
		String[] list = value.split(sep);
		if(trim) {
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
		if(type == String.class) {
			return (T)value;
		}
		
		//ENUM OBJECT
		if(type.isEnum()) {
			return	(T)Enum.valueOf(type.asSubclass(Enum.class), value);
		}
		
		//ARRAY OF OBJECTS
		if(type == String[].class) {
			return (T)toArray(value, ",", true);
		} else if(type.isArray()) {
			ArrayList<Object> arr = new ArrayList<>();
			for(String s: toArray(value, ",", true)) {
				arr.add(toObject(s, type.getComponentType()));
			}
			return (T)arr;
		}
		
		//PRIMITIVE OBJECT
		Converter<?> c = TYPES.get(type);
		if(c != null) {
			return (T)c.to(value);
		}
		
		//UNKNOW TYPE => RETURN AS IS!!!
		return (T)value;
	}
}
