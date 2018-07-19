package com.appe.framework.util;

import java.util.List;

/**
 * A simple collection of list items with total count
 * 
 * @author ho
 *
 * @param <T>
 */
public class SubList<T> {
	private int count;
	private List<T> items;
	public SubList() {
	}
	
	/**
	 * return a total sublist
	 * @param items
	 */
	public SubList(List<T> items) {
		this.items = items;
		this.count = (items == null? 0 : items.size());
	}
	
	/**
	 * return partial sublist
	 * 
	 * @param items
	 * @param fromIndex
	 * @param toIndex
	 */
	public SubList(List<T> items, int fromIndex, int toIndex) {
		this.items = (items == null? null : items.subList(fromIndex, toIndex));
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
	public SubList<T> withCount(int count) {
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
	public SubList<T> withItems(List<T> items) {
		setItems(items);
		return this;
	}
}
