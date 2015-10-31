package com.appe.security.internal;

import java.security.Principal;
import java.util.Set;

import com.appe.security.Authorization;
import com.appe.util.Objects;
/**
 * Grant with list of permissions/roles to access system. It's have to be a GRANT TYPE.
 * 
 * @author tobi
 */
public class AuthorizedGrant extends Authorization {
	private static final Set<String> EMPTY_ROLES = Objects.asSet();
	
	private Principal	principal;
	private	Set<String>	roles;
	/**
	 * Grant authentication with permission set associated with.
	 * @param principal
	 * @param roles
	 */
	public AuthorizedGrant(Principal principal, Set<String> roles) {
		this.principal = principal;
		this.roles 	   = roles;
	}
	
	/**
	 * 
	 * @param principal
	 */
	public AuthorizedGrant(Principal principal) {
		this(principal, EMPTY_ROLES);
	}
	
	/**
	 * return the original principal can be a STRING...
	 * @return
	 */
	@Override
	public Principal getPrincipal() {
		return principal;
	}
	
	/**
	 * return all roles
	 * @return
	 */
	@Override
	public Set<String> getRoles() {
		return roles;
	}
}
