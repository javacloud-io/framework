package io.javacloud.framework.json.internal;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.javacloud.framework.util.Dictionaries;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.Pair;

/**
 * Implements JsonPath spec: https://goessner.net/articles/JsonPath
 * Also supporting {}
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
			dict = getProperty(dict, segments[i]);
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
				v = new Dictionary();
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
				dict = Dictionaries.asDict(p.getValue(), dict);
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
	protected String[] segments(String path) {
		String[] segments = path.split("\\.");
		return segments;
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
	public static final boolean is(String path) {
		return !Objects.isEmpty(path) && path.startsWith(ROOT);
	}
}
