package com.appe.security.oauth2;
/**
 * Standard OAuth2 constants definition, there will be a mapping across vendors...
 * 
 * @author ho
 *
 */
public interface IdPConstants {
	//SPECIAL REDIRECT URI INDICATE INSTALLED APPLICATION
	public static final String OOB_REDIRECT_URI		= "urn:ietf:wg:oauth:2.0:oob";
	
	//REQUEST PARAMETERs
	public static final String PARAM_RESPONSE_TYPE 	= "response_type";
	public static final String PARAM_CLIENT_ID 		= "client_id";
	public static final String PARAM_CLIENT_SECRET 	= "client_secret";
	public static final String PARAM_REDIRECT_URI 	= "redirect_uri";
	public static final String PARAM_GRANT_TYPE 	= "grant_type";
	
	public static final String PARAM_SCOPE 			= "scope";
	public static final String PARAM_STATE 			= "state";
	public static final String PARAM_ERROR 			= "error";
	
	public static final String PARAM_ACCESS_CODE 	= "code";
	public static final String PARAM_ACCESS_TOKEN 	= "access_token";
	public static final String PARAM_TOKEN_TYPE 	= "token_type";
	public static final String PARAM_EXPIRES_IN 	= "expires_in"; //UTC seconds
	public static final String PARAM_USERNAME 		= "username";
	public static final String PARAM_PASSWORD 		= "password";
	
	public static final String GRANT_AUTHZ_CODE 	= "authorization_code";
	public static final String GRANT_PASSWORD 		= "password";
	public static final String GRANT_CLIENT_CREDS	= "client_credentials";
	
	public static final String RESPONSE_TOKEN 		= "token";
	public static final String RESPONSE_CODE 		= "code";
	
	public static final String SCHEME_BASIC 		= "Basic";	//Client Basic
	public static final String SCHEME_BEARER		= "Bearer";	//Oauth2 Bearer
	
	//ERROR CODEs
	public static final String ERROR_SERVER_ERROR 	= "server_error";
	public static final String ERROR_INVALID_REQUEST= "invalid_request";
	public static final String LOGIN_REDIRECT_URI	= "/login";
}
