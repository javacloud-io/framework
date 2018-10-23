package io.javacloud.framework.json.internal;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Stack;

import io.javacloud.framework.util.Dictionaries;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.Pair;

/**
 * https://goessner.net/articles/JsonPath
 * 
 * @author ho
 *
 */
public class JsonPath {
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
			dict = getField(dict, segments[i]);
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
		if(isRoot(path)) {
			this.root = value;
			return Objects.cast(value);
		}
		
		//FORWARD UNTIL NOT ABLE TO SET
		Object dict = this.root;
		Stack<Pair<Object, String>> stack = new Stack<>();
		String[] segments = segments(path);
		for(int i = 1; i < segments.length; i ++) {
			Object v = getField(dict, segments[i]);
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
			if(!setField(p.getKey(), p.getValue(), dict)) {
				dict = Dictionaries.asDict(p.getValue(), dict);
			} else {
				dict = p.getKey();
			}
		}
		this.root = dict;
		return Objects.cast(this.root);
	}
	
	/**
	 * 
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
	protected Object getField(Object v, String name) {
		if(v instanceof Map<?,?>) {
			return ((Map<?,?>)v).get(name);
		}
		
		//ASSUMING PROPERTY NOT FOUND
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, v.getClass());
			return pd.getReadMethod().invoke(v);
		}catch(Exception ex) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param v
	 * @param name
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	protected boolean setField(Object v, String name, Object value) {
		if(v instanceof Map<?,?>) {
			((Map<String, Object>)v).put(name, value);
			return true;
		}
		
		//ASSUMING PROPERTY NOT FOUND
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, v.getClass());
			pd.getWriteMethod().invoke(v, value);
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static final boolean isRoot(String path) {
		return (path == null || path.isEmpty() || path.equals("$"));
	}
}
