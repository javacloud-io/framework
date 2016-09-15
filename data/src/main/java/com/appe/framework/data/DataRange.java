package com.appe.framework.data;

/**
 * Slice apply to range key with some limit setup. Perfect for simple QUERY. It's the SAME AS PAGE concept.
 * Each query will start with PAGE.
 * 
 * @author tobi
 *
 */
public class DataRange {
	public static final int DEFAULT_LIMIT = 100;
	
	private int 		limit;				//limit
	private boolean 	reversed;			//reversed
	private	DataKey		exclusiveStartKey;	//exclusive start token, return from previous result.
	/**
	 * 
	 * @param limit
	 * @param reversed
	 * @param exclusiveStartKey
	 */
	public DataRange(int limit, boolean reversed, DataKey exclusiveStartKey) {
		this.limit 		= limit;
		this.reversed 	= reversed;
		this.exclusiveStartKey 	= exclusiveStartKey;
	}
	
	/**
	 * 
	 * @param limit
	 */
	public DataRange(int limit) {
		this(limit, false, null);
	}
	
	/**
	 * Using default range scan
	 */
	public DataRange() {
		this(DEFAULT_LIMIT, false, null);
	}
	
	/**
	 * 
	 * @return
	 */
	public DataKey getExclusiveStartKey() {
		return exclusiveStartKey;
	}

	public void setExclusiveStartKey(DataKey exclusiveStartKey) {
		this.exclusiveStartKey = exclusiveStartKey;
	}

	public DataRange withExclusiveStartKey(DataKey exclusiveStartKey) {
		this.exclusiveStartKey = exclusiveStartKey;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public DataRange withLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isReversed() {
		return reversed;
	}
	
	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}
	
	public DataRange withReserved(boolean reversed) {
		this.reversed = reversed;
		return this;
	}
}
