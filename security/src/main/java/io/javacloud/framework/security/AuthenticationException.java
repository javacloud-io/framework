package io.javacloud.framework.security;

import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.UncheckedException;
/**
 * 
 * @author aimee
 *
 */
public class AuthenticationException extends UncheckedException {
	private static final long serialVersionUID = -3499627145582890978L;
	
	//STANDARD ERROR CODES
	public static final String UNAUTHORIZED_CLIENT 	= "unauthorized_client";
	public static final String INVALID_CREDENTIALS 	= "invalid_credentials";
	public static final String EXPIRED_CREDENTIALS 	= "expired_credentials";
	public static final String INVALID_SCOPE 		= "invalid_scope";
	public static final String ACCESS_DENIED 		= "access_denied";
	/**
	 * 
	 * @param cause
	 */
	public AuthenticationException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 */
	public AuthenticationException(String message) {
		super(message);
	}
	
	/**
	 * ALWAYS USING MESSAGE AS REASON CODE
	 */
	@Override
	public String getCode() {
		String message = super.getMessage();
		return Objects.isEmpty(message) ? super.getCode() : message;
	}
}
