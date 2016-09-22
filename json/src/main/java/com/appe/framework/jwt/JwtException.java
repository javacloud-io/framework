package com.appe.framework.jwt;

import com.appe.framework.util.ValidationException;
/**
 * 
 * @author ho
 *
 */
public class JwtException extends ValidationException {
	private static final long serialVersionUID = -1070362326324298497L;
	public static final String INVALID_TOKEN   = "invalid_jwt_token";
	
	/**
	 * 
	 * @param message
	 */
	public JwtException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param t
	 */
	public JwtException(Throwable t) {
		super(INVALID_TOKEN, t);
	}
	
	/**
	 * 
	 */
	public JwtException() {
		super(INVALID_TOKEN);
	}
}
