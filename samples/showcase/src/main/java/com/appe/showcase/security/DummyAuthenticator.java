/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.showcase.security;

import java.security.Principal;
import java.util.Set;

import javax.inject.Singleton;

import com.appe.authz.Authentication;
import com.appe.authz.AuthenticationException;
import com.appe.authz.Authenticator;
import com.appe.authz.InvalidCredentialsException;
import com.appe.util.Objects;
/**
 * Any valid credentials is OK!
 * 
 * @author ho
 *
 */
@Singleton
public class DummyAuthenticator implements Authenticator {

	@Override
	public Authentication authenticate(final Principal credentials) throws AuthenticationException {
		if(credentials == null) {
			throw new InvalidCredentialsException();
		}
		
		final Set<String> roles = Objects.asSet("user");
		return new Authentication() {
			@Override
			public Principal getPrincipal() {
				return credentials;
			}
			
			@Override
			public Set<String> getRoles() {
				return roles;
			}
		};
	}
}
