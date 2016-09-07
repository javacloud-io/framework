package com.appe.framework.security.impl;

import java.security.Principal;

import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.Authenticator;
import com.appe.framework.security.Authorization;
import com.appe.framework.security.internal.AuthorizationGrant;
import com.appe.framework.security.internal.Credentials;
import com.appe.framework.security.internal.Permissions;
import com.appe.framework.util.Objects;
/**
 * Anybody can be authenticate again with name/credentials are the same. Useful for testing
 * 
 * @author ho
 *
 */
public class SampleAuthenticator implements Authenticator {
	@Override
	public Authorization authenticate(Principal credentials) throws AuthenticationException {
		if(credentials instanceof Credentials) {
			Credentials creds = (Credentials)credentials;
			if(creds.getName() != null && creds.getName().equals(creds.getSecret())) {
				return new AuthorizationGrant(creds, Objects.asSet(Permissions.ROLE_USER));
			}
		}
		return null;
	}
}
