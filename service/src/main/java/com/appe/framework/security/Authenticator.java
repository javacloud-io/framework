package com.appe.framework.security;

import java.security.Principal;

/**
 * To authenticate almost anything with simple credentials.
 * 
 * @author ho
 *
 */
public interface Authenticator {
	/**
	 * Authenticate credentials and return new one if success which contains more fine grant system wide permissions.
	 * return null if credentials is not appropriated to handle.
	 * 
	 * @param credentials
	 * @return
	 * @throws AuthenticationException
	 */
	public Authorization authenticate(Principal credentials) throws AuthenticationException;
}
