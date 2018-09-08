package io.javacloud.framework.security.internal;

import io.javacloud.framework.security.AccessGrant;

import java.security.Principal;
import java.util.Set;
/**
 * Grant with list of permissions/roles to access system. It's have to be a GRANT TYPE.
 * 
 * @author tobi
 */
public class AuthorizationGrant implements AccessGrant {
	private Principal	subject;
	private	Set<String>	roles;
	
	private String		audience;
	private String 		scope;
	/**
	 * Grant authentication with permission set associated with.
	 * @param subject
	 * @param roles
	 */
	public AuthorizationGrant(Principal subject, Set<String> roles) {
		this.subject = subject;
		this.roles  = roles;
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
	public Set<String> getRoles() {
		return roles;
	}
	
	/**
	 * 
	 */
	@Override
	public String getAudience() {
		return audience;
	}
	public AuthorizationGrant withAudience(String audience) {
		this.audience = audience;
		return this;
	}
	
	/**
	 * 
	 */
	@Override
	public String getScope() {
		return scope;
	}
	/**
	 * 
	 * @param scope
	 */
	public AuthorizationGrant withScope(String scope) {
		this.scope = scope;
		return this;
	}
}
