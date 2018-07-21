package com.appe.framework.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.appe.framework.io.Converter;
import com.appe.framework.io.Dictionary;

/**
 * Basic primary object converter
 * 
 * @author tobi
 *
 */
public final class Objects {
	//PRIMITIVES MAP
	public static final Map<Class<?>, Converter<?>> PRIMITIVES = asMap(
			boolean.class, 	Converters.BOOLEAN,
			Boolean.class, 	Converters.BOOLEAN,
			byte.class,    	Converters.BYTE,
			Byte.class, 	Converters.BYTE,
			char.class,		Converters.CHARACTER,
			Character.class,Converters.CHARACTER,
			short.class,	Converters.SHORT,
			Short.class,	Converters.SHORT,
			int.class,		Converters.INTEGER,
			Integer.class,	Converters.INTEGER,
			long.class,		Converters.LONG,
			Long.class,		Converters.LONG,
			float.class,	Converters.FLOAT,
			Float.class,	Converters.FLOAT,
			double.class,	Converters.DOUBLE,
			Double.class,	Converters.DOUBLE,
			String.class,	Converters.STRING,
			Date.class,		Converters.DATE,
			byte[].class,	Converters.BYTES,
			ByteBuffer.class,Converters.BYTEB
	);
	/**
	 * Generic comparing 2 objects. It has to be comparable some how.
	 * NULL <= NULL < NOT NULL
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final Comparator<Object> COMPARER = new Comparator() {
		@Override
		public int compare(Object v1, Object v2) {
			//OK, NULL OUT FIRST
			if(v1 == null) {
				return (v2 == null? 0 : -1);
			} else if(v2 == null) {
				return (v1 == null? 0 : 1);
			}
			
			//USING NATIVE COMPARE
			if(v1 instanceof Comparable) {
				return ((Comparable)v1).compareTo(v2);
			} else if(v2 instanceof Comparable) {
				return -((Comparable)v2).compareTo(v1);
			}
			
			//GIVE UP? USING HASH
			return signum(v1.hashCode() - v2.hashCode());
		}
	};
	
	//PROTECTED
	private Objects() {
	}
	
	/**
	 * Shortcut to compare 2 OBJECTs.
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static int compare(Object o1, Object o2) {
		return COMPARER.compare(o1, o2);
	}
	
	/**
	 * Generic cast the OBJECT without warning
	 * @param v
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o) {
		return (T)o;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String toString(Object value) {
		return Converters.STRING.to(value);
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
		buf.append(Converters.STRING.to(list[0]));
		for(int i = 1; i < list.length; i ++) {
			buf.append(sep)
			   .append(Converters.STRING.to(list[i]));
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
	 * Make sure to add them all UP.
	 * @param values
	 * @return
	 */
	@SafeVarargs
	public static <T> List<T> asList(T... values) {
		ArrayList<T> list = new ArrayList<>();
		if(values != null && values.length > 0) {
			for(T value: values) {
				if(value != null) {
					list.add(value);
				}
			}
		}
		return list; 
	}
	
	/**
	 * Convert enumerated value to SET.
	 * @param values
	 * @return
	 */
	@SafeVarargs
	public static <T> Set<T> asSet(T... values) {
		return	new HashSet<T>(asList(values));
	}
	
	/**
	 * Transform name/value list to a MAP. values.length has to be even elements
	 * 
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> asMap(Object... values) {
		Map<K, V> map = new LinkedHashMap<K, V>();
		if(values != null && values.length > 1) {
			for(int i = 1; i < values.length; i += 2) {
				V value = (V)values[i];
				if(value != null) {
					map.put((K)values[i - 1], value);
				}
			}
		}
		return map;
	}
	
	/**
	 * Quick wrapping dictionary object.
	 * @param values
	 * @return
	 */
	public static Dictionary asDict(Object... values) {
		Map<String, Object> map = asMap(values);
		return new Dictionary(map);
	}
	
	/**
	 * Trying to sleep up to duration unless interrupted.
	 * @param duration
	 * @param unit
	 * @return true if no interrupted
	 */
	public static boolean sleep(long duration, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(duration));
			return true;
		}catch(InterruptedException ex) {
			//IGNORE EX
		}
		return false;
	}
	
	/**
	 * return sign of the number
	 * @param num
	 * @return
	 */
	public static int signum(long num) {
		return (num > 0? 1: (num < 0? -1 : 0));
	}
	
	/**
	 * If the string is empty or NOT.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
	
	/**
	 * Check if collections is empty
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	/**
	 * return true if list is empty
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(Object[] value) {
		return value == null || value.length == 0;
	}
	
	/**
	 * return true if collection is empty
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	/**
	 * Quietly close the streams without any exception...
	 * 
	 * @param closable
	 */
	public static void closeQuietly(java.io.Closeable... closables) {
		for(java.io.Closeable c: closables) {
			if(c != null) {
				try {
					c.close();
				}catch(Exception ex) {
					//NOOP
				}
			}
		}
	}
}
