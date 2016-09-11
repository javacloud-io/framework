package com.appe.framework.security;

import java.security.Principal;

/**
 * Generic principal which can hold anything. Just implements principal for anything.
 * 
 * @author aimee
 *
 */
public abstract class Authorization implements AccessGrant {
	protected Authorization() {
		
	}
	
	/**
	 * BY DEFAULT RETURN THE DISPLAY NAME as PRINCIPAL!
	 */
	@Override
	public String getName() {
		Principal principal = getPrincipal();
		return (principal == null? null : principal.getName());
	}
	
	/**
	 * Intended of the target which claim is good for. Normally it's an application who making call.
	 * 
	 * @return
	 */
	@Override
	public String getAudience() {
		Principal principal = getPrincipal();
		return (principal instanceof AccessGrant ? ((AccessGrant)principal).getAudience() : null);
	}
	
	/**
	 * return the delegate principal if any associated besides this.
	 * 
	 * @return
	 */
	public abstract Principal getPrincipal();
}
