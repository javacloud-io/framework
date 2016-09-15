package com.appe.framework.data;

import com.appe.framework.AppeException;
/**
 * 
 * @author tobi
 *
 */
public class DataException extends AppeException {
	private static final long serialVersionUID = -1497852484857281383L;
	/**
	 * 
	 * @param cause
	 */
	protected DataException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 */
	public DataException(String message) {
		super(message);
	}
	
	/**
	 * Make sure to always return a single AppeException instead of stack of them.
	 * @param t
	 */
	public static DataException wrap(Throwable t) {
		if(t instanceof DataException) {
			return	(DataException)t;
		}
		return	new DataException(t);
	}
}
