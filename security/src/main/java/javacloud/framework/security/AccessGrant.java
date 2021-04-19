package javacloud.framework.security;

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
	Principal getSubject();
	
	/**
	 * Client in which this is grant all
	 * 
	 * @return
	 */
	String getAudience();
	
	/**
	 * Set of scope which grant might have access
	 * 
	 * @return
	 */
	String getScope();
	
	/**
	 * Set of static roles entitle to the grant
	 * @return
	 */
	Set<String> getRoles();
}
