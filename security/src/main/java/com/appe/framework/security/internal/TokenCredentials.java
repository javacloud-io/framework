package com.appe.framework.security.internal;



/**
 * Authentication using oauth2 Bearer token base, user/client already exchanged for token.
 * A default NULL issuer if token is local
 * 
 * @author tobi
 *
 */
public class TokenCredentials extends BasicCredentials {
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
