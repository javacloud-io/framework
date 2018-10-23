package io.javacloud.framework.json.internal;

import java.util.Map;

import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;

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
	public static final boolean isRoot(String path) {
		return (path == null || path.isEmpty() || path.equals("$"));
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public <T> T select(String path) {
		if(isRoot(path)) {
			return Objects.cast(root);
		}
		if(! (root instanceof Map<?,?>)) {
			return null;
		}
		Map<String, Object> dict = Objects.cast(root);
		String[] segments = path.split("\\.");
		for(int i = segments[0].equals("$")? 1 : 0; i < segments.length - 1; i ++) {
			Object v = dict.get(segments[i]);
			if(! (v instanceof Map<?, ?>)) {
				return null;
			}
			dict = Objects.cast(v);
		}
		return Objects.cast(dict.get(segments[segments.length - 1]));
	}
	
	/**
	 * 
	 * @param path: reference path
	 * @param value
	 * @return
	 */
	public <T, V> T merge(String path, V value) {
		if(isRoot(path)) {
			this.root = value;
			return Objects.cast(value);
		}
		
		//CREATE ROOT IF NEED
		Map<String, Object> dict;
		if(this.root instanceof Map<?, ?>) {
			dict = Objects.cast(this.root);
		} else {
			dict = new Dictionary();
			this.root = dict;
		}
		String[] segments = path.split("\\.");
		for(int i = segments[0].equals("$")? 1 : 0; i < segments.length - 1; i ++) {
			Object v = dict.get(segments[i]);
			if(! (v instanceof Map<?, ?>)) {
				v = new Dictionary();
				dict.put(segments[i], v);
			}
			dict = Objects.cast(v);
		}
		return Objects.cast(this.root);
	}
}
