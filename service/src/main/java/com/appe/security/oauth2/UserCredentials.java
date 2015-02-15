package com.appe.security.oauth2;

import com.appe.security.SimpleCredentials;

/**
 * Represent username & password of credentials.
 * 
 * @author ho
 *
 */
public class UserCredentials extends SimpleCredentials {
	public UserCredentials(String name, String secret) {
		super(name, secret);
	}
	public UserCredentials(String base64Token) {
		super(base64Token);
	}
}
