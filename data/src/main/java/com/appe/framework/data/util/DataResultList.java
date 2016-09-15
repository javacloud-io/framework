package com.appe.framework.data.util;

import java.util.List;

import com.appe.framework.data.DataKey;
import com.appe.framework.data.DataResult;
/**
 * Simple result wrapping around list & lastEvaluated key
 * 
 * @author tobi
 *
 */
public class DataResultList<T> implements DataResult<T> {
	private List<T> items;
	private DataKey lastEvaluatedKey;
	/**
	 * 
	 */
	protected DataResultList() {
	}
	
	/**
	 * 
	 * @param items
	 */
	public DataResultList(List<T> items) {
		this(items, null);
	}
	
	/**
	 * 
	 * @param items
	 * @param lastEvaluatedKey
	 */
	public DataResultList(List<T> items, DataKey lastEvaluatedKey) {
		this.items = items;
		this.lastEvaluatedKey = lastEvaluatedKey;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public int count() {
		return items.size();
	}
	public int getCount() {
		return count();
	}
	
	/**
	 * return token can be use to query next page.
	 */
	@Override
	public DataKey lastEvaluatedKey() {
		return lastEvaluatedKey;
	}
	
	/**
	 * return list of items
	 * @return
	 */
	@Override
	public List<T> items() {
		return items;
	}
	
	//MAKE SURE TO BE ABLE TO JSONIZE
	public List<T> getItems() {
		return items();
	}
	public void setItems(List<T> items) {
		this.items = items;
	}
	
	//MAKE SURE TO BE ABLE TO JSONIZE
	public DataKey getLastEvaluatedKey() {
		return lastEvaluatedKey;
	}
	public void setLastEvaluatedKey(DataKey lastEvaluatedKey) {
		this.lastEvaluatedKey = lastEvaluatedKey;
	}

	/**
	 * Just dump list and next token for debug purpose.
	 */
	@Override
	public String toString() {
		return "{" + items + (lastEvaluatedKey != null? ", lastEvaluatedKey=" + lastEvaluatedKey : "") + "}";
	}
}
