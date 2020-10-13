package javacloud.framework.security;

/**
 * Denied access due to not enough permission in some case.
 * 
 * @author tobi
 *
 */
public class AccessDeniedException extends AuthenticationException {
	private static final long serialVersionUID = 5341577128925410681L;
	/**
	 * 
	 */
	public AccessDeniedException() {
		this(ACCESS_DENIED);
	}
	
	/**
	 * 
	 * @param message
	 */
	public AccessDeniedException(String message) {
		super(message);
	}
}
