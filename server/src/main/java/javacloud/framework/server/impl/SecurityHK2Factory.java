package javacloud.framework.server.impl;

import java.security.Principal;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.hk2.api.Factory;

import javacloud.framework.security.AccessGrant;
/**
 * For easy integrate with jersey resource allow Authorization to be inject using @Inject or @Context
 * 
 * @author ho
 *
 */
public class SecurityHK2Factory implements Factory<AccessGrant> {
	private SecurityContext securityContext;
	
	@Inject
    public SecurityHK2Factory(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }
	
	/**
	 * return authorization from attribute if any found.
	 */
	@Override
	public AccessGrant provide() {
		Principal principal = securityContext.getUserPrincipal();
		return	(principal instanceof AccessGrant? (AccessGrant)principal: null);
	}
	
	/**
	 * 
	 */
	@Override
	public void dispose(AccessGrant authz) {
	}
}
