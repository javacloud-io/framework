package com.appe.framework.io;

import java.util.Map;
/**
 * Quick implementation of MAP entry to provide a LIST style.
 * 
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
