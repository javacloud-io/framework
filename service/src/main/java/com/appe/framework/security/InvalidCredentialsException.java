package com.appe.framework.security;

import com.appe.framework.security.AuthenticationException;
/**
 * 
 * @author tobi
 *
 */
public class InvalidCredentialsException extends AuthenticationException {
	private static final long serialVersionUID = 5341577128925410681L;
	/**
	 * 
	 */
	public InvalidCredentialsException() {
		this(INVALID_CREDENTIALS);
	}
	
	/**
	 * 
	 * @param message
	 */
	public InvalidCredentialsException(String message) {
		super(message);
	}
}
