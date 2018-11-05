package javacloud.framework.security;

import javacloud.framework.util.Objects;
import javacloud.framework.util.ValidationException;
/**
 * 
 * @author aimee
 *
 */
public class AuthenticationException extends ValidationException {
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
	public String getReason() {
		String message = super.getMessage();
		return Objects.isEmpty(message) ? super.getReason() : message;
	}
}
