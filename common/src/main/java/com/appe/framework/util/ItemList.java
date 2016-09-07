package com.appe.framework.util;

import java.util.List;

/**
 * A simple collection of list items with total count
 * 
 * @author ho
 *
 * @param <T>
 */
public class ItemList<T> {
	private int count;
	private List<T> items;
	public ItemList() {
	}
	
	/**
	 * 
	 * @param items
	 */
	public ItemList(List<T> items) {
		this.items = items;
		this.count = (items == null? 0 : items.size());
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * 
	 * @param count
	 * @return
	 */
	public ItemList<T> withCount(int count) {
		setCount(count);
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}
	
	/**
	 * 
	 * @param items
	 * @return
	 */
	public ItemList<T> withItems(List<T> items) {
		setItems(items);
		return this;
	}
}
