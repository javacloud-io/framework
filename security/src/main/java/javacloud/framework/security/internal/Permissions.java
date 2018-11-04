package javacloud.framework.security.internal;

import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AccessGrant;
import javacloud.framework.util.Objects;

import java.util.Set;


/**
 * Global permissions system grant access to generic area of the service. Too complicated at this level to manage
 * access to individual resource. Lower level will manage to do so at the right context.
 * 
 * @author ho
 *
 */
public final class Permissions {
	public static final Set<String> EMPTY_ROLES 	= Objects.asSet();
	
	//PRE-DEFINED USER roles
	public static final String ROLE_ROOT			= "root";
	public static final String ROLE_ADMINISTRATOR	= "administrator";
	
	//any valid user in the system
	public static final String ROLE_DEVELOPER		= "developer";
	public static final String ROLE_USER			= "user";
	
	//any agent based valid client_id
	public static final String ROLE_CLIENT			= "client";
		
	//untrusted client, native/browser with KEY
	public static final String ROLE_UNTRUSTED_CLIENT= "untrusted_client";
	
	//trusted server client with KEY
	public static final String ROLE_TRUSTED_CLIENT	= "trusted_client";
	private Permissions() {
	}
	
	/**
	 * See if the granted authorization has any permissions asking for.
	 * 
	 * @param authz
	 * @param permissions
	 * @return
	 * @throws AccessDeniedException
	 */
	public static String assertAny(AccessGrant authz, String... permissions)
		throws AccessDeniedException {
		if(authz == null || authz.getSubject() == null || !hasAny(authz, permissions)) {
			throw new AccessDeniedException();
		}
		return authz.getSubject().getName();
	}
	
	/**
	 * See if granted authorization not has any permissions asking for
	 * 
	 * @param authz
	 * @param permissions
	 * @return
	 * @throws AccessDeniedException
	 */
	public static String assertNone(AccessGrant authz, String... permissions)
		throws AccessDeniedException {
		if(authz == null || authz.getSubject() == null || hasAny(authz, permissions)) {
			throw new AccessDeniedException();
		}
		return authz.getSubject().getName();
	}
	
	/**
	 * See if the granted authorization has all permissions asking for.
	 * 
	 * @param authz
	 * @param permissions
	 * @return
	 * @throws AccessDeniedException
	 */
	public static String assertAll(AccessGrant authz, String... permissions)
		throws AccessDeniedException {
		if(authz == null || authz.getSubject() == null || !hasAll(authz, permissions)) {
			throw new AccessDeniedException();
		}
		return authz.getSubject().getName();
	}
	
	/**
	 * return true if authentication has all permission
	 * @param authz
	 * @param permission
	 * @return
	 */
	public static boolean hasAll(AccessGrant authz, String ...roles) {
		//NOT ANY PERMISSIONs
		Set<String> claims = authz.getRoles();
		if(claims == null || claims.isEmpty()) {
			return false;
		}
				
		//HAS NOTHING
		if(roles == null || roles.length == 0) {
			return true;
		}
		
		//SCAN THEM ALL
		for(String name: roles) {
			if(!claims.contains(name)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * return true if has any role which is the basic of all use cases.
	 * @param authz
	 * @param roles
	 * @return
	 */
	public static boolean hasAny(AccessGrant authz, String ...roles) {
		//NOT ANY PERMISSIONS
		Set<String> claims = authz.getRoles();
		if(claims == null || claims.isEmpty()) {
			return false;
		}
				
		//NOTHING AT ALL
		if(roles == null || roles.length == 0) {
			return true;
		}
		
		//FIND ONE IS ENOUGH
		for(String name: roles) {
			if(claims.contains(name)) {
				return true;
			}
		}
		return false;
	}
}
