package io.javacloud.framework.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A simple collection of list items with total count
 * 
 * @author ho
 *
 * @param <T>
 */
public class ResultList<T> implements Iterable<T> {
	private int count;
	private List<T> items;
	public ResultList() {
	}
	/**
	 * return a total sublist
	 * @param items
	 */
	public ResultList(List<T> items) {
		this(items, items == null? 0 : items.size());
	}
	
	/**
	 * 
	 * @param items
	 * @param count
	 */
	public ResultList(List<T> items, int count) {
		this.count = count;
		this.items = items;
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
	public ResultList<T> withCount(int count) {
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
	public ResultList<T> withItems(List<T> items) {
		setItems(items);
		return this;
	}
	
	/**
	 * return internal iterator
	 */
	@Override
	public Iterator<T> iterator() {
		if(items == null) {
			return Collections.emptyIterator();
		}
		return items.iterator();
	}
}
