package com.appe.showcase.security;

import java.security.Principal;
import java.util.Set;

import com.appe.security.AuthenticationProvider;
import com.appe.security.Authorization;
import com.appe.security.AuthorizationException;
import com.appe.security.InvalidCredentialsException;
import com.appe.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class DummyAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authorization authenticate(final Principal credentials) throws AuthorizationException {
		if(credentials == null) {
			throw new InvalidCredentialsException();
		}
		
		final Set<String> permissions = Objects.asSet("User");
		return new Authorization() {
			@Override
			public Principal getPrincipal() {
				return credentials;
			}
			
			@Override
			public Set<String> getPermissions() {
				return permissions;
			}
		};
	}
}
