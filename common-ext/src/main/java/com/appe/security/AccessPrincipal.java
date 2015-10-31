package com.appe.security;

import java.security.Principal;

/**
 * Remote principal access to endpoint
 * 
 * @author ho
 *
 */
public interface AccessPrincipal extends Principal {
	/**
	 * 
	 * @return
	 */
	public String getAudience();
}
