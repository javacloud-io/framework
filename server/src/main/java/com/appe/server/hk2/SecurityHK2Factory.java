package com.appe.server.hk2;

import java.security.Principal;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.hk2.api.Factory;

import com.appe.security.Authorization;
/**
 * For easy integrate with jersey resource allow Authorization to be inject using @Inject or @Context
 * 
 * @author ho
 *
 */
public class SecurityHK2Factory implements Factory<Authorization> {
	private SecurityContext securityContext;
	
	@Inject
    public SecurityHK2Factory(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }
	
	/**
	 * return authorization from attribute if any found.
	 */
	@Override
	public Authorization provide() {
		Principal principal = securityContext.getUserPrincipal();
		return	(principal instanceof Authorization? (Authorization)principal: null);
	}
	
	/**
	 * 
	 */
	@Override
	public void dispose(Authorization authz) {
	}
}
