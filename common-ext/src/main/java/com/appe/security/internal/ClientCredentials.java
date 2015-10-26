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
package com.appe.security.internal;



/**
 * Special oauth2 client credentials which carry out of of other factor.
 * 
 * @author tobi
 *
 */
public class ClientCredentials extends BasicCredentials {
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
		return getExtra(IdPConstants.PARAM_REDIRECT_URI);
	}
	
	/**
	 * Current asking scope
	 * @return
	 */
	public String getScope() {
		return getExtra(IdPConstants.PARAM_SCOPE);
	}
}
