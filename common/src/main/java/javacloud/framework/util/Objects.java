package javacloud.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Basic primary object converter
 * 
 * @author tobi
 *
 */
public final class Objects {
	/**
	 * Generic comparing 2 objects. It has to be comparable some how.
	 * NULL <= NULL < NOT NULL
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final Comparator<Object> COMPARATOR = (v1, v2) -> {
		//OK, NULL OUT FIRST
		if (v1 == null) {
			return (v2 == null? 0 : -1);
		} else if (v2 == null) {
			return (v1 == null? 0 : 1);
		}
		
		//USING NATIVE COMPARE
		if (v1 instanceof Comparable) {
			return ((Comparable)v1).compareTo(v2);
		} else if (v2 instanceof Comparable) {
			return -((Comparable)v2).compareTo(v1);
		}
		
		//GIVE UP? USING HASH
		return signum(v1.hashCode() - v2.hashCode());
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
		return COMPARATOR.compare(o1, o2);
	}
	
	/**
	 * If the string is empty or NOT.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return (value == null || value.isEmpty());
	}
	
	/**
	 * Check if collections is empty
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
	
	/**
	 * 
	 * @param value
	 * @return true if list is empty
	 */
	public static boolean isEmpty(Object[] collection) {
		return (collection == null || collection.length == 0);
	}
	
	/**
	 * 
	 * @param collection
	 * @return true if collection is empty
	 */
	public static boolean isEmpty(Map<?, ?> collection) {
		return (collection == null || collection.isEmpty());
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return true if a == b
	 */
	public static boolean equals(Object a, Object b) {
		return (a == b) || (a != null && a.equals(b));
	}
	
	/**
	 * 
	 * @param num
	 * @return sign of the number
	 */
	public static int signum(long num) {
		return (num > 0? 1: (num < 0? -1 : 0));
	}
	
	/**
	 * 
	 * @param ta
	 * @param tb
	 * @return similarity between string with value [0,1] similar to string compare
	 */
	public static float similarity(String ta, String tb) {
		int la = ta.length();
		int lb = tb.length();
		
		//one characters can't match set of 2
		if(la == 0 || lb == 0) {
			return (la == lb? 1.0f : 0);
		}
		//count matches in both
		int lm = Math.min(la, lb);
		int nt = 0;
		while (nt < lm && ta.charAt(nt) == tb.charAt(nt)) {
			nt ++;
		}
		return (2.0f * nt) / (la + lb);
	}
	
	/**
	 * Generic cast the OBJECT without warning
	 * 
	 * @param v
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o) {
		return (T)o;
	}
	
	/**
	 * Make sure to add them all UP.
	 * 
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
	 * 
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
			return false;
		}
	}
	
	/**
	 * Quietly close the streams without any exception...
	 * 
	 * @param closable
	 */
	public static boolean closeQuietly(AutoCloseable closable) {
		try {
			if(closable != null) {
				closable.close();
				return true;
			}
		} catch(Exception ex) {}
		return false;
	}
}
