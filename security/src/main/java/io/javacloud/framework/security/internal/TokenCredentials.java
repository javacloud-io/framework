package io.javacloud.framework.security.internal;

import io.javacloud.framework.security.IdParameters;



/**
 * Authentication using oauth2 Bearer token base, user/client already exchanged for token.
 * 
 * @author tobi
 *
 */
public class TokenCredentials extends BasicCredentials {
	/**
	 * 
	 * @param type
	 * @param token
	 */
	public TokenCredentials(IdParameters.GrantType type, String token) {
		super(type.name(), token);
	}
	
	/**
	 * return grant type of token
	 * 
	 * @return
	 */
	public IdParameters.GrantType getType() {
		return IdParameters.GrantType.valueOf(getName());
	}
	
	/**
	 * shot cut to return token credentials
	 * @return
	 */
	public String getToken() {
		return getSecret();
	}
}
