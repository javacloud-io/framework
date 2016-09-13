package com.appe.framework.security.internal;

import com.appe.framework.security.Credentials;




/**
 * Represent username & password of credentials.
 * 
 * @author ho
 *
 */
public class PasswordCredentials extends Credentials {
	public PasswordCredentials(String name, String secret) {
		super(name, secret);
	}
	public PasswordCredentials(String base64Token) {
		super(base64Token);
	}
}
