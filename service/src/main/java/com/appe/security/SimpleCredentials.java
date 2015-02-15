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
package com.appe.security;

import com.appe.sec.Codecs;
import com.appe.util.Dictionary;
/**
 * Simple authentication request, just principal & credentials. There are remoteAddress field help to identify original.
 * 
 * @author tobi
 *
 */
public class SimpleCredentials extends SimplePrincipal {
	private	String 		secret;
	private Dictionary 	extras;
	
	/**
	 * 
	 * @param name
	 * @param secret
	 */
	public SimpleCredentials(String name, String secret) {
		super(name);
		this.secret = secret;
	}
	
	/**
	 * Passing base64(principal:credentials)
	 * 
	 * @param base64Token
	 */
	public SimpleCredentials(String base64Token) {
		String stoken = Codecs.encodeUTF8(Codecs.decodeBase64(base64Token, false));
		int index = stoken.indexOf(':');
		if(index >= 0) {
			this.name = stoken.substring(0, index);
			this.secret = stoken.substring(index + 1);
		} else {
			this.name = stoken;
		}
	}
	
	/**
	 * return the credentials secret
	 * 
	 * @return
	 */
	public String getSecret() {
		return secret;
	}
	
	/**
	 * Shortcut to authentication context, can be any arbitrary thing.
	 * @param name
	 * @param value
	 * @return
	 */
	public SimpleCredentials withExtra(String name, Object value) {
		//CREATE ONE IF NOT EXIST
		if(extras == null) {
			extras = new Dictionary();
		}
		extras.set(name, value);
		return this;
	}
	
	/**
	 * Get the context value of given thing
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getExtra(String name) {
		return (T)(extras == null? null : extras.get(name));
	}
}
