package javacloud.framework.json.internal;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.util.Objects;
import javacloud.framework.util.Pair;

/**
 * Basic implements JsonPath specification : https://goessner.net/articles/JsonPath
 * 
 * -DOT access: $.books
 * -INDEX access: $.books[0]
 * -RANGE access: $.books[0:5]
 * 
 * @author ho
 *
 */
public class JsonPath {
	private static final Logger logger = Logger.getLogger(JsonPath.class.getName());
	public static final String ROOT = "$";
	
	private Object root;
	public JsonPath(Object root) {
		this.root = root;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public <T> T select(String path) {
		if(isRoot(path)) {
			return Objects.cast(this.root);
		}
		String[] segments = segments(path);
		Object dict = this.root;
		for(int i = 1; i < segments.length; i ++) {
			dict = resolveProperty(dict, segments[i]);
			if(dict == null) {
				break;
			}
		}
		return Objects.cast(dict);
	}
	
	/**
	 * 
	 * @param path
	 * @param value
	 * @return
	 */
	public <T, V> T merge(String path, V value) {
		//REPLACE ROOT
		if(isRoot(path)) {
			return Objects.cast(value);
		}
		
		//FORWARD UNTIL NOT ABLE TO SET
		Object dict = this.root;
		Stack<Pair<Object, String>> stack = new Stack<>();
		String[] segments = segments(path);
		for(int i = 1; i < segments.length; i ++) {
			Object v = getProperty(dict, segments[i]);
			if(v == null) {
				v = Objects.asMap();
			}
			stack.push(new Pair<Object, String>(dict, segments[i]));
			dict = v;
		}
		
		//START FROM BOTTOM TRY TO SET VALUE
		dict = value;
		while(!stack.isEmpty()) {
			Pair<Object, String> p = stack.pop();
			//NOT ABLE TO SET => CONVERT TO DICT
			if(!setProperty(p.getKey(), p.getValue(), dict)) {
				dict = Objects.asMap(p.getValue(), dict);
			} else {
				dict = p.getKey();
			}
		}
		return Objects.cast(dict);
	}
	
	/**
	 * [1] based index
	 * @param path
	 * @return
	 */
	private String[] segments(String path) {
		String[] segments = path.split("\\.");
		return segments;
	}
	
	/**
	 * resolve field[*], field[s:e]
	 * 
	 * @param json
	 * @param name
	 * @return
	 */
	private Object resolveProperty(Object json, String name) {
		int s = name.indexOf('[');
		if(s < 0) {
			return getProperty(json, name);
		}
		int e = name.indexOf(']', s);
		if(e < 0) {
			throw new ArrayIndexOutOfBoundsException("Index not close with ]");
		}
		
		//WHOLE OBJECT
		Object v = getProperty(json, name.substring(0, s));
		if(v == null) {
			return v;
		}
		String index = name.substring(s + 1, e);
		int d = index.indexOf(':');
		if(d == 0 && index.length() <= 1) {
			return v;
		}
		
		//PROCESS INDEX
		boolean isArray = v.getClass().isArray();
		List<Object> list = isArray? Objects.asList((Object[])v) : Objects.cast(v);
		if(d < 0) {
			s = intValueOf(index, list.size());
			return list.get(s);
		}
		
		//PROCESS RANGE
		if(d == 0) {
			//SUB[:e]
			s = 0;
			e = intValueOf(index.substring(1), list.size());
		} else if(d == index.length() - 1) {
			//SUB[s:]
			s = intValueOf(index.substring(0, d), list.size());
			e = list.size();
		} else {
			//SUB[s:e]
			s = intValueOf(index.substring(0, d), list.size());
			e = intValueOf(index.substring(d + 1),list.size());
		}
		
		//SUBLIST OR WHOLE
		list = (s == 0 && e == list.size()? list : list.subList(s,  e));
		return isArray? list.toArray() : list;
	}
	
	/**
	 * 
	 * @param index
	 * @param len
	 * @return
	 */
	private int intValueOf(String index, int len) {
		if(Objects.isEmpty(index)) {
			return 0;
		}
		int i = Integer.valueOf(index);
		return (i < 0? i + len : i);
	}
	
	/**
	 * 
	 * @param v
	 * @param name
	 * @return
	 */
	protected Object getProperty(Object json, String name) {
		//MAP
		if(json instanceof Map<?,?>) {
			return ((Map<?,?>)json).get(name);
		}
		
		//ASSUMING PROPERTY NOT FOUND
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, json.getClass());
			return pd.getReadMethod().invoke(json);
		}catch(Exception ex) {
			logger.log(Level.WARNING, "Unable to get property: " + name, ex);
			return null;
		}
	}
	
	/**
	 * 
	 * @param json
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean setProperty(Object json, String name, Object value) {
		//MAP
		if(json instanceof Map<?,?>) {
			((Map<String, Object>)json).put(name, value);
			return true;
		}
		
		//ASSUMING PROPERTY NOT FOUND
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, json.getClass());
			pd.getWriteMethod().invoke(json, value);
			return true;
		}catch(Exception ex) {
			logger.log(Level.WARNING, "Unable to set property: " + name, ex);
			return false;
		}
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static final boolean isRoot(String path) {
		return (Objects.isEmpty(path) || path.equals(ROOT));
	}
	
	/**
	 * return true if reference path
	 * @param path
	 * @return
	 */
	public static final boolean isRef(String path) {
		return !Objects.isEmpty(path) && path.startsWith(ROOT);
	}
}
