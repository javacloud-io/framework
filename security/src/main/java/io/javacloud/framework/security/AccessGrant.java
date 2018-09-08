package io.javacloud.framework.security;

import java.security.Principal;
import java.util.Set;

/**
 * A grant with set of claims for given subject and target audience. Claims include:
 * -SUBJECT
 * -ROLES
 * -SCOPE
 * -AUDIENCE
 * 
 * @author ho
 *
 */
public interface AccessGrant extends Principal {
	/**
	 * User/Client... of the grant
	 * 
	 * @return
	 */
	public Principal getSubject();
	
	/**
	 * Client in which this is grant all
	 * 
	 * @return
	 */
	public String getAudience();
	
	/**
	 * Set of scope which grant might have access
	 * 
	 * @return
	 */
	public String getScope();
	
	/**
	 * Set of static roles entitle to the grant
	 * @return
	 */
	public Set<String> getRoles();
}
