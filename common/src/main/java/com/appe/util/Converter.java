/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.util;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import com.appe.AppeException;
import com.appe.sec.Codecs;

/**
 * Converting from something to something else.
 * 
 * @author tobi
 * @param <T>
 */
public interface Converter<T> {
	/**
	 * Convert an object v to any type T, value is string or sub type of T.
	 * @param value
	 * @return
	 */
	public T convert(Object value);
	
	//ANYTHING
	public static final Converter<Object> OBJECT = new Converter<Object>() {
		@Override
		public Object convert(Object value) {
			return value;
		}
	};
	
	//BOOLEAN | NUMBER | STRING
	public static final Converter<Boolean> BOOLEAN = new Converter<Boolean>() {
		@Override
		public Boolean convert(Object value) {
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
		public Byte convert(Object value) {
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
		public Character convert(Object value) {
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
		public Integer convert(Object value) {
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
		public Short convert(Object value) {
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
		public Long convert(Object value) {
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
		public Float convert(Object value) {
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
		public Double convert(Object value) {
			if(value instanceof Double) {
				return (Double)value;
			} else if(value instanceof Number) {
				return ((Number)value).doubleValue();
			}
			return (value != null ? Double.valueOf((String)value) : null);
		}
	};
	//BYTES | BYTED | BASE64 ENCODED?
	public static final Converter<ByteBuffer> BYTEB = new Converter<ByteBuffer>() {
		@Override
		public ByteBuffer convert(Object value) {
			if(value instanceof ByteBuffer) {
				return (ByteBuffer)value;
			} else if(value instanceof byte[]) {
				return ByteBuffer.wrap((byte[])value);
			}
			
			//ASSUMING BASE64 STRING ENCODED?
			return (value != null ? ByteBuffer.wrap(Codecs.decodeBase64((String)value, false)) : null);
		}
	};
	//DATE
	public static final Converter<Date> DATE = new Converter<Date>() {
		@Override
		public Date convert(Object value) {
			if(value instanceof Date) {
				return	(Date)value;
			} else if(value instanceof Number) {
				return new Date(((Number)value).longValue());
			}
			//USING ISO DATE
			try {
				return (value != null? DateFormats.get(DateFormats.DFL_ISO8601).parse((String)value) : null);
			}catch(ParseException ex) {
				throw AppeException.wrap(ex);
			}
		}
	};
	//STRING
	public static final Converter<String> STRING = new Converter<String>() {
		@Override
		public String convert(Object value) {
			if(value instanceof String) {
				return	(String)value;
			} else if(value instanceof Date) {
				return DateFormats.get(DateFormats.DFL_ISO8601).format((Date)value);
			} else if(value instanceof byte[]) {
				return Codecs.encodeBase64((byte[])value, false);
			} else if(value instanceof Object[]) {
				return	Arrays.asList((Object[])value).toString();
			}
			return (value != null? value.toString() : null);
		}
	};
}
