package com.appe.framework.util;

import com.appe.framework.AppeException;
/**
 * Throw exception if something not found.
 * 
 * @author tobi
 *
 */
public class NotFoundException extends AppeException {
	private static final long serialVersionUID = -1655317657875278373L;
	
	public NotFoundException(String subject) {
		super(subject);
	}
}
