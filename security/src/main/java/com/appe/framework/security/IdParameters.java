package com.appe.framework.security;

/**
 * Standard OAuth2 constants definition, there will be a mapping across vendors...
 * 
 * @author ho
 *
 */
public interface IdParameters extends Credentials {
	//REQUEST PARAMETERs
	public static final String PARAM_CLIENT_ID 		= "client_id";
	public static final String PARAM_CLIENT_SECRET 	= "client_secret";
	public static final String PARAM_REDIRECT_URI 	= "redirect_uri";
	public static final String PARAM_GRANT_TYPE 	= "grant_type";
	public static final String PARAM_RESPONSE_TYPE 	= "response_type";
	
	public static final String PARAM_SCOPE 			= "scope";
	public static final String PARAM_STATE 			= "state";
	public static final String PARAM_ERROR 			= "error";
	
	public static final String PARAM_AUTHORIZATION_CODE 	= "code";
	public static final String PARAM_ACCESS_TOKEN 	= "access_token";
	public static final String PARAM_TOKEN_TYPE 	= "token_type";
	public static final String PARAM_EXPIRES_IN 	= "expires_in"; //UTC seconds
	public static final String PARAM_USERNAME 		= "username";
	public static final String PARAM_PASSWORD 		= "password";
	
	//SPECIAL REDIRECT URI INDICATE INSTALLED APPLICATION
	public static final String OOB_REDIRECT_URI		= "urn:ietf:wg:oauth:2.0:oob";
	public static final String LOGIN_REDIRECT_URI	= "/login";
	
	//ERROR CODEs
	public static final String ERROR_SERVER_ERROR 	= "server_error";
	public static final String ERROR_INVALID_REQUEST= "invalid_request";
	
	//Schemes
	public static enum SchemeType {
		Basic,	//Client Basic
		Bearer;	//Oauth2 Bearer
	}
	
	//type of response
	public static enum ResponseType {
		token,
		code
	}
	
	//type of grant
	public static enum GrantType {
		password,
		client_credentials,
		
		authorization_code,
		access_token,
		refresh_token
	}
	
	/**
	 * return requested client
	 * @return
	 */
	public String getClientId();
	
	/**
	 * return client requested secret
	 * @return
	 */
	public String getClientSecret();
	
	/**
	 * URI to handle the authorization token
	 * @return
	 */
	public String getRedirectURI();
	
	/**
	 * intended response type
	 * 
	 * @return
	 */
	public String getResponseType();
	
	/**
	 * space delimiter of scope of token, which service is asking for
	 * @return
	 */
	public String getScope();
	
	/**
	 * return turn the state passing around
	 * @return
	 */
	public String getState();
	
	/**
	 * return requested grant type
	 * @return
	 */
	public String getGrantType();
	
	/**
	 * return authorization code if grant type is authorization_code
	 * @return
	 */
	public String getCode();
	
	/**
	 * return requested username if grant type is password
	 * @return
	 */
	public String getUsername();
	
	/**
	 * requested password if grant type is password 
	 * @return
	 */
	public String getPassword();
}
