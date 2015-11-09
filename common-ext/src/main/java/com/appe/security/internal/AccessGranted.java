package com.appe.security.internal;

import java.security.Principal;
import java.util.Set;

import com.appe.security.Authorization;
/**
 * Grant with list of permissions/roles to access system. It's have to be a GRANT TYPE.
 * 
 * @author tobi
 */
public class AccessGranted extends Authorization {
	private Principal	principal;
	private	Set<String>	roles;
	/**
	 * Grant authentication with permission set associated with.
	 * @param principal
	 * @param roles
	 */
	public AccessGranted(Principal principal, Set<String> roles) {
		this.principal = principal;
		this.roles 	   = roles;
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