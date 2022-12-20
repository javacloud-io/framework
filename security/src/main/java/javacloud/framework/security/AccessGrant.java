package javacloud.framework.security;

import java.security.Principal;
import java.util.Map;
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
	 * Set of static roles entitle to the grant
	 * 
	 * @return
	 */
	Set<String> getRoles();
	
	/**
	 * 
	 * @return custom claims
	 */
	Map<String, Object> getClaims();
}
