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
package com.appe.security.impl;

import com.appe.security.SimpleCredentials;

/**
 * Authentication using oauth2 Bearer token base, user/client already exchanged for token.
 * 
 * @author tobi
 *
 */
public class TokenCredentials extends SimpleCredentials {
	public TokenCredentials(String token) {
		super(null, token);
	}
	
	/**
	 * shot cut to return token credentials
	 * @return
	 */
	public String getToken() {
		return getSecret();
	}
}
