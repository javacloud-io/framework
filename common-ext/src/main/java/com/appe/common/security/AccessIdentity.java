package com.appe.security;

import java.security.Principal;

/**
 * Remote principal access to endpoint
 * 
 * @author ho
 *
 */
public interface AccessIdentity extends Principal {
	/**
	 * 
	 * @return
	 */
	public String getAudience();
}
