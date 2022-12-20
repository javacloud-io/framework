package javacloud.framework.security.internal;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import javacloud.framework.security.AccessGrant;
/**
 * Grant with list of permissions/roles to access system. It's have to be a GRANT TYPE.
 * 
 * @author tobi
 */
public class AuthorizationGrant implements AccessGrant {
	private Principal	subject;
	private	Set<String>	roles;
	private	Map<String, Object>	claims;
	
	/**
	 * Grant authentication with permission set associated with.
	 * @param subject
	 * @param roles
	 */
	public AuthorizationGrant(Principal subject, Set<String> roles) {
		this.subject = subject;
		this.roles = roles;
	}
	
	/**
	 * 
	 */
	@Override
	public String getName() {
		return (subject == null? null : subject.getName());
	}

	/**
	 * @return the original principal can be a STRING...
	 */
	@Override
	public Principal getSubject() {
		return subject;
	}
	
	/**
	 * @return all roles
	 */
	@Override
	public Set<String> getRoles() {
		return roles;
	}
	
	@Override
	public Map<String, Object> getClaims() {
		return claims;
	}

	public AuthorizationGrant withClaims(Map<String, Object> claims) {
		this.claims = claims;
		return this;
	}
}
