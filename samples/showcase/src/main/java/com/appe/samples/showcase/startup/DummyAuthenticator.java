package com.appe.samples.showcase.startup;

import java.security.Principal;

import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.Authenticator;
import com.appe.framework.security.Authorization;
import com.appe.framework.security.AuthorizationGrant;
import com.appe.framework.security.Permissions;
import com.appe.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class DummyAuthenticator implements Authenticator {
	@Override
	public Authorization authenticate(Principal credentials) throws AuthenticationException {
		return new AuthorizationGrant(credentials , Objects.asSet(Permissions.ROLE_USER));
	}
}
