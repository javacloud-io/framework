package com.appe.security.internal;

import com.appe.security.AccessDeniedException;
import com.appe.security.Authorization;


/**
 * Global permissions system grant access to generic area of the service. Too complicated at this level to manage
 * access to individual resource. Lower level will manage to do so at the right context.
 * 
 * @author ho
 *
 */
public final class Permissions {
	//PRE-DEFINED USER roles
	public static final String ROLE_ROOT			= "root";
	public static final String ROLE_ADMINISTRATOR	= "administrator";
	public static final String ROLE_DEVELOPER		= "developer";
	
	//any valid user in the system
	public static final String ROLE_USER			= "user";
	
	//any valid client_id
	public static final String ROLE_CLIENT			= "client";
		
	//untrusted client, native/browser with KEY
	public static final String ROLE_UNTRUSTED_CLIENT= "untrusted_client";
	
	//trusted server client
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
	public static String assertAny(Authorization authz, String... permissions)
		throws AccessDeniedException {
		if(authz == null || !authz.hasAnyRoles(permissions)) {
			throw new AccessDeniedException();
		}
		return authz.getName();
	}
	
	/**
	 * See if granted authorization not has any permissions asking for
	 * 
	 * @param authz
	 * @param permissions
	 * @return
	 * @throws AccessDeniedException
	 */
	public static String assertNone(Authorization authz, String... permissions)
		throws AccessDeniedException {
		if(authz == null || authz.hasAnyRoles(permissions)) {
			throw new AccessDeniedException();
		}
		return authz.getName();
	}
	
	/**
	 * See if the granted authorization has all permissions asking for.
	 * 
	 * @param authz
	 * @param permissions
	 * @return
	 * @throws AccessDeniedException
	 */
	public static String assertAll(Authorization authz, String... permissions)
		throws AccessDeniedException {
		if(authz == null || !authz.hasAllRoles(permissions)) {
			throw new AccessDeniedException();
		}
		return authz.getName();
	}
}
