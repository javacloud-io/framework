package com.appe.framework.security;

import java.security.Principal;

/**
 * Standard OAuth2 constants definition, there will be a mapping across vendors...
 * 
 * @author ho
 *
 */
public interface IdParameters extends Principal {
	//Schemes
	public static enum Scheme {
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
		authorization_code,
		password,
		client_credentials,
		refresh_token
	}
	
	//REQUEST PARAMETERs
	public static final String PARAM_CLIENT_ID 		= "client_id";
	public static final String PARAM_CLIENT_SECRET 	= "client_secret";
	public static final String PARAM_REDIRECT_URI 	= "redirect_uri";
	public static final String PARAM_GRANT_TYPE 	= "grant_type";
	public static final String PARAM_RESPONSE_TYPE 	= "response_type";
	
	public static final String PARAM_SCOPE 			= "scope";
	public static final String PARAM_STATE 			= "state";
	public static final String PARAM_ERROR 			= "error";
	
	public static final String PARAM_ACCESS_CODE 	= "code";
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
	
	/**
	 * 
	 * @return
	 */
	public String getClientId();
	
	/**
	 * 
	 * @return
	 */
	public String getClientSecret();
	
	/**
	 * 
	 * @return
	 */
	public String getRedirectURI();
	
	/**
	 * 
	 * @return
	 */
	public String getResponseType();
	
	/**
	 * 
	 * @return
	 */
	public String getScope();
	
	/**
	 * 
	 * @return
	 */
	public String getState();
	
	/**
	 * 
	 * @return
	 */
	public String getGrantType();
	
	/**
	 * 
	 * @return
	 */
	public String getCode();
	
	/**
	 * 
	 * @return
	 */
	public String getUsername();
	
	/**
	 * 
	 * @return
	 */
	public String getPassword();
}
