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

import java.util.List;

/**
 * A simple collection of list items with total count
 * 
 * @author ho
 *
 * @param <T>
 */
public class ResultList<T> {
	private List<T> items;
	private int count;
	public ResultList() {
	}
	
	/**
	 * 
	 * @param items
	 */
	public ResultList(List<T> items) {
		this.items = items;
		this.count = (items == null? 0 : items.size());
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
	 * @param items
	 * @return
	 */
	public ResultList<T> withItems(List<T> items) {
		setItems(items);
		return this;
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
}
