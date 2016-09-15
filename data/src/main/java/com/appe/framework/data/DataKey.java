package com.appe.framework.data;

import com.appe.framework.util.Converter;
import com.appe.framework.util.Objects;

/**
 * Simple representation of both hash &| range key. HASH key always required!!!
 * 
 * @author tobi
 */
public class DataKey implements Comparable<DataKey> {
	private Object hashKey;
	private Comparable<?> rangeKey;
	/**
	 * Make sure range key is comparable always
	 * @param hashKey
	 * @param rangeKey
	 */
	public DataKey(Object hashKey, Comparable<?> rangeKey) {
		this.hashKey = hashKey;
		this.rangeKey= rangeKey;
	}
	
	/**
	 * Just a hash key
	 * @param hashKey
	 */
	public DataKey(Object hashKey) {
		this(hashKey, null);
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <H> H getHashKey() {
		return (H)hashKey;
	}
	
	/**
	 * 
	 * @param hashKey
	 */
	public <H> void setHashKey(H hashKey) {
		this.hashKey = hashKey;
	}
	
	/**
	 * 
	 * @param hashKey
	 * @return
	 */
	public <H> DataKey withHashKey(H hashKey) {
		this.hashKey = hashKey;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <R> Comparable<R> getRangeKey() {
		return (Comparable<R>)rangeKey;
	}
	
	/**
	 * 
	 * @param rangeKey
	 */
	public <R> void setRangeKey(Comparable<R> rangeKey) {
		this.rangeKey = rangeKey;
	}
	
	/**
	 * 
	 * @param rangeKey
	 * @return
	 */
	public <R> DataKey withRangeKey(Comparable<R> rangeKey) {
		this.rangeKey = rangeKey;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasRangeKey() {
		return rangeKey != null;
	}
	
	/**
	 * Make sure key with HASH/RANGE the SAME.
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(!(obj instanceof DataKey)) {
			return false;
		}
		
		//COMPARE HASH
		DataKey key = (DataKey)obj;
		if(hashKey == null) {
			return (key.hashKey == null);
		}
		
		if(!hashKey.equals(key.hashKey)) {
			return false;
		}
		
		//COMPARE RANGE
		if(rangeKey == null) {
			return (key.rangeKey == null);
		}
		
		//THE SAME
		return rangeKey.equals(key.rangeKey);
	}
	
	/**
	 * Trying to compare the HASH/RANGE KEY.
	 */
	@Override
	public int compareTo(DataKey key) {
		int diff = Objects.compare(hashKey, key.hashKey);
		if(diff == 0) {
			diff = Objects.compare(rangeKey, key.rangeKey);
		}
		return diff;
	}
	
	/**
	 * return the hash code combine.
	 */
	@Override
	public int hashCode() {
		return 31 * (hashKey == null ? 0 : hashKey.hashCode())
			   + (rangeKey == null? 0 : rangeKey.hashCode()); 
	}
	
	/**
	 * Using hash/range key toString.
	 */
	@Override
	public String toString() {
		return Converter.STRING.convert(hashKey)
					+ (rangeKey != null? "#" + Converter.STRING.convert(rangeKey) : "");
	}
}