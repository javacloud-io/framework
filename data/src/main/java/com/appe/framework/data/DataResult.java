package com.appe.framework.data;

import java.util.List;

/**
 * Simple data result which can be advance automatically if need.
 * 
 * 
 * @author tobi
 * @param <T>
 */
public interface DataResult<T> {
	/**
	 * return a next result list if still have.
	 * @return
	 */
	public List<T> items();
	
	/**
	 * Total count of current list if dynamic somehow?
	 * @return
	 */
	public int	count();
	
	/**
	 * return next token which can pass to next range call.
	 * @return
	 */
	public DataKey lastEvaluatedKey();
}
