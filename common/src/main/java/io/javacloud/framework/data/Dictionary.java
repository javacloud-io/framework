package io.javacloud.framework.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Retain the order of name/value added to the dictionary by default
 * 
 * @author ho
 *
 */
public final class Dictionary implements Map<String, Object> {
	private final Map<String, Object> impl;
	/**
	 * Default to a simple HASH MAP.
	 */
	public Dictionary() {
		this(new LinkedHashMap<String, Object>());
	}
	
	/**
	 * Switch backing map to use new impl
	 * @param impl
	 */
	public Dictionary(Map<String, Object> impl) {
		this.impl = impl;
	}
	
	/**
	 *  Cast to any object type, caller has to make sure cast is OK.
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		return (T)impl.get(name);
	}
	
	/**
	 * return default value if NULL
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public <T> T get(String name, T defaultValue) {
		T value = get(name);
		return (value == null ? defaultValue : value);
	}
	
	/**
	 * No value return AT ALL.
	 * @param name
	 * @param value
	 */
	public <T> void set(String name, T value) {
		impl.put(name, value);
	}
	
	/**
	 * return all enumeration names
	 * @return
	 */
	public Enumeration<String> keyNames() {
		return Collections.enumeration(keySet());
	}

	
	//////////////////AUTO GENERATED FOR DELEGATION/////////////////////
	@Override
	public int size() {
		return impl.size();
	}

	@Override
	public boolean isEmpty() {
		return impl.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return impl.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return impl.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return impl.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return impl.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return impl.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		impl.putAll(m);
	}

	@Override
	public void clear() {
		impl.clear();
	}

	@Override
	public Set<String> keySet() {
		return impl.keySet();
	}

	@Override
	public Collection<Object> values() {
		return impl.values();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return impl.entrySet();
	}
	
	@Override
	public String toString() {
		return impl.toString();
	}
}
