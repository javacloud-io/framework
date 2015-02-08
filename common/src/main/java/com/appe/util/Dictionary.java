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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;
/**
 * Implements Config using MAP. Make sure it's OK on the other side of the PROJECT.
 * 
 * @author aimee
 */
public class Dictionary implements Map<String, Object> {
	private Map<String, Object> store;
	/**
	 * Default to a simple HASH MAP.
	 */
	public Dictionary() {
		this(new LinkedHashMap<String, Object>());
	}
	
	/**
	 * Allow to configure different MAP memory.
	 * @param store
	 */
	public Dictionary(Map<String, Object> store) {
		this.store = store;
	}
	
	/**
	 *  Cast to any object type, caller has to make sure cast is OK.
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		return (T)store.get(name);
	}
	
	/**
	 * No value return AT ALL.
	 * @param name
	 * @param value
	 */
	public final void set(String name, Object value) {
		put(name, value);
	}
	
	/**
	 * Trying to return the string for this OBJECT.
	 * 
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		return Converter.STRING.convert(get(name));
	}
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String getString(String name, String defaultValue) {
		String value = getString(name);
		return (value == null ? defaultValue : value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Boolean getBoolean(String name) {
		return Converter.BOOLEAN.convert(get(name));
	}
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		Boolean value = getBoolean(name);
		return (value == null ? defaultValue : value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Integer getInteger(String name) {
		return Converter.INTEGER.convert(get(name));
	}
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public int getInteger(String name, int defaultValue) {
		Integer value = getInteger(name);
		return (value == null ? defaultValue : value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Long getLong(String name) {
		return Converter.LONG.convert(get(name));
	}
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String name, long defaultValue) {
		Long value = getLong(name);
		return (value == null ? defaultValue : value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Float getFloat(String name) {
		return Converter.FLOAT.convert(get(name));
	}
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public float getFloat(String name, float defaultValue) {
		Float value = getFloat(name);
		return (value == null ? defaultValue : value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Double getDouble(String name) {
		return Converter.DOUBLE.convert(get(name));
	}
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public double getDouble(String name, double defaultValue) {
		Double value = getDouble(name);
		return (value == null ? defaultValue : value);
	}
	
	/**
	 * Convert to string the whole map!
	 * Ideally can use the PropertiesMapper to convert to XML.
	 */
	@Override
	public String toString() {
		return store.toString();
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return store.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return store.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return store.entrySet();
	}
	
	//MAKE SURE NOT OVERRIDE ABLE
	@Override
	public final Object get(Object key) {
		return get((String)key);
	}

	@Override
	public boolean isEmpty() {
		return store.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return store.keySet();
	}

	@Override
	public Object put(String name, Object value) {
		return	store.put(name, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		store.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return store.remove(key);
	}

	@Override
	public int size() {
		return store.size();
	}

	@Override
	public Collection<Object> values() {
		return store.values();
	}
}
