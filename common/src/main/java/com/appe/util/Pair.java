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

import java.util.Map;
/**
 * Quick implementation of MAP entry to provide a LIST style.
 * @author tobi
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K,V> implements Map.Entry<K, V> {
	private K key;
	private V value;
	/**
	 * 
	 * @param key
	 */
	public Pair(K key) {
		this(key, null);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public Pair(K key, V value) {
		this.key   = key;
		this.value = value;
	}
	
	/**
	 * return key
	 */
	@Override
	public K getKey() {
		return key;
	}
	
	/**
	 * return the value
	 */
	@Override
	public V getValue() {
		return value;
	}
	
	/**
	 * Set new value and return the OLD one if exist.
	 */
	@Override
	public V setValue(V value) {
		V v = this.value;
		this.value = value;
		return v;
	}
}
