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
package com.appe.framework.security.internal;



/**
 * Authentication using oauth2 Bearer token base, user/client already exchanged for token.
 * A default NULL issuer if token is local
 * @author tobi
 *
 */
public class TokenCredentials extends Credentials {
	/**
	 * Local token
	 * 
	 * @param token
	 */
	public TokenCredentials(String token) {
		this(null, token);
	}
	
	/**
	 * External token with issuer
	 * 
	 * @param issuer
	 * @param token
	 */
	public TokenCredentials(String issuer, String token) {
		super(issuer, token);
	}

	/**
	 * shot cut to return token credentials
	 * @return
	 */
	public String getToken() {
		return getSecret();
	}
}
