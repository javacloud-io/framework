package com.appe.security.internal;

import com.appe.security.AccessIdentity;
/**
 * An access principal
 * 
 * @author ho
 *
 */
public class AccessPrincipal extends Credentials implements AccessIdentity {
	/**
	 * 
	 * @param name
	 * @param audience
	 */
	public AccessPrincipal(String name, String audience) {
		super(name, audience);
	}
	
	/**
	 * 
	 * @param credentials
	 * @param audience
	 */
	public AccessPrincipal(Credentials credentials, String audience) {
		super(credentials.getName(), audience);
		withAttributes(credentials);
	}
	
	/**
	 * 
	 */
	@Override
	public String getAudience() {
		return getSecret();
	}
}
