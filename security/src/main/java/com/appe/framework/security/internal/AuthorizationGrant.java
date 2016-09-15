package com.appe.framework.security.internal;

import java.security.Principal;
import java.util.Set;

import com.appe.framework.security.AccessGrant;
/**
 * Grant with list of permissions/roles to access system. It's have to be a GRANT TYPE.
 * 
 * @author tobi
 */
public class AuthorizationGrant implements AccessGrant {
	private Principal	subject;
	private	Set<String>	claims;
	/**
	 * Grant authentication with permission set associated with.
	 * @param subject
	 * @param roles
	 */
	public AuthorizationGrant(Principal subject, Set<String> claims) {
		this.subject = subject;
		this.claims  = claims;
	}
	
	/**
	 * 
	 */
	@Override
	public String getName() {
		return (subject != null? subject.getName() : null);
	}

	/**
	 * return the original principal can be a STRING...
	 * @return
	 */
	@Override
	public Principal getSubject() {
		return subject;
	}
	
	/**
	 * return all roles
	 * @return
	 */
	@Override
	public Set<String> getClaims() {
		return claims;
	}
}
