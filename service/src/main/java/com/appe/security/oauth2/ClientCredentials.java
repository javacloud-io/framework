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
package com.appe.security.oauth2;

import com.appe.security.SimpleCredentials;

/**
 * Special oauth2 client credentials which carry out of of other factor.
 * 
 * @author tobi
 *
 */
public class ClientCredentials extends SimpleCredentials {
	public static final String _CLIENT_ID	 = "clientId";
	public static final String _CLIENT_SECRET= "clientSecret";
	public static final String _REDIRECT_URI = "redirectURI";
	public static final String _RESPONSE_TYPE= "responseType";
	public static final String _SCOPE 		 = "scope";
	public static final String _STATE		 = "state";
	/**
	 * 
	 * @param clientId
	 * @param clientSecret
	 */
	public ClientCredentials(String clientId, String clientSecret) {
		super(clientId, clientSecret);
	}
	
	/**
	 * 
	 * @param base64Token
	 */
	public ClientCredentials(String base64Token) {
		super(base64Token);
	}
	
	/**
	 * return redirect URI if any set
	 * @return
	 */
	public String getRedirectURI() {
		return getExtra(_REDIRECT_URI);
	}
	
	/**
	 * Current asking scope
	 * @return
	 */
	public String getScope() {
		return getExtra(_SCOPE);
	}
}
