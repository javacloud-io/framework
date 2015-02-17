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

import com.appe.security.Authenticator;
import com.appe.security.Authorization;
import com.appe.security.AuthorizationException;
import com.appe.security.InvalidCredentialsException;
import com.appe.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class DummyAuthenticator implements Authenticator {

	@Override
	public Authorization authenticate(final Principal credentials) throws AuthorizationException {
		if(credentials == null) {
			throw new InvalidCredentialsException();
		}
		
		final Set<String> permissions = Objects.asSet("user");
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
