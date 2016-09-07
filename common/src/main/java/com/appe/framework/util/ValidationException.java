package com.appe.framework.util;

import com.appe.framework.AppeException;
/**
 * TODO: add special details about the validation
 * 
 * @author ho
 *
 */
public class ValidationException extends AppeException {
	private static final long serialVersionUID = 4869699128152046268L;

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(String message) {
		super(message);
	}
}
