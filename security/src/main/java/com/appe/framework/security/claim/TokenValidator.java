package com.appe.framework.security.claim;

import com.appe.framework.security.AuthenticationException;

/**
 * Help to validate if a token is valid and return GRANT associated. So it can be run in detached mode.
 * 
 * @author ho
 *
 */
public interface TokenValidator {
	/**
	 * return the access token if any found.
	 * 
	 * @param token
	 * @throw AuthenticationException
	 * @return
	 */
	public	TokenGrant	validateToken(String token) throws AuthenticationException;
}
