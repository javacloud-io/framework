package com.appe.framework.security;

/**
 * Special oauth2 client credentials which carry out of of other factor.
 * 
 * @author tobi
 *
 */
public class ClientCredentials extends Credentials {
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
		return getAttribute(IdParameters.PARAM_REDIRECT_URI);
	}
	
	/**
	 * Current asking scope
	 * @return
	 */
	public String getScope() {
		return getAttribute(IdParameters.PARAM_SCOPE);
	}
}
