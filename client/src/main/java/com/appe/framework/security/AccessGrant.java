package com.appe.framework.security;

import java.security.Principal;

/**
 * Remote principal access to an endpoint
 * 
 * @author ho
 *
 */
public interface AccessGrant extends Principal {
	/**
	 * 
	 * @return
	 */
	public String getAudience();
}
