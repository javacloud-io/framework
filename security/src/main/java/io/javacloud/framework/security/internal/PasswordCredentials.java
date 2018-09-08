package io.javacloud.framework.security.internal;





/**
 * Represent username & password of credentials.
 * 
 * @author ho
 *
 */
public class PasswordCredentials extends BasicCredentials {
	public PasswordCredentials(String name, String secret) {
		super(name, secret);
	}
	public PasswordCredentials(String base64Token) {
		super(base64Token);
	}
}
