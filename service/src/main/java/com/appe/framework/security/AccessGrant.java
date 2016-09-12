package com.appe.framework.security;

import java.security.Principal;
import java.util.Set;

/**
 * Remote principal access to an endpoint
 * 
 * @author ho
 *
 */
public interface AccessGrant extends Principal {
	/**
	 * User/Client... of the grant
	 * 
	 * @return
	 */
	public Principal getSubject();
	
	/**
	 * return the permission set of this authentication, by default it will be NONE.
	 * ONLY GRANTED authentication should have claims.
	 * 
	 * @return
	 */
	public Set<String> getClaims();
}
