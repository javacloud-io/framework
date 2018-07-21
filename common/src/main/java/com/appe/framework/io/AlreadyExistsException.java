package com.appe.framework.io;

import com.appe.framework.AppeException;
/**
 * Throws the exception if something already exist.
 * 
 * @author tobi
 * 
 */
public class AlreadyExistsException extends AppeException {
	private static final long serialVersionUID = -1725603370191594789L;
	
	public AlreadyExistsException(String subject) {
		super(subject);
	}
}
