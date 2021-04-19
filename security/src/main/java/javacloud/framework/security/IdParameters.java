package javacloud.framework.security;

/**
 * Standard OAuth2 constants definition, there will be a mapping across vendors...
 * 
 * @author ho
 *
 */
public interface IdParameters extends Credentials {
	//REQUEST PARAMETERs
	String PARAM_CLIENT_ID 		= "client_id";
	String PARAM_CLIENT_SECRET 	= "client_secret";
	String PARAM_REDIRECT_URI 	= "redirect_uri";
	String PARAM_GRANT_TYPE 	= "grant_type";
	String PARAM_RESPONSE_TYPE 	= "response_type";
	
	String PARAM_SCOPE 			= "scope";
	String PARAM_STATE 			= "state";
	String PARAM_ERROR 			= "error";
	
	String PARAM_AUTHORIZATION_CODE = "code";
	String PARAM_ACCESS_TOKEN 		= "access_token";
	String PARAM_TOKEN_TYPE 		= "token_type";
	String PARAM_EXPIRES_IN 		= "expires_in"; //UTC seconds
	String PARAM_USERNAME 			= "username";
	String PARAM_PASSWORD 			= "password";
	
	//SPECIAL REDIRECT URI INDICATE INSTALLED APPLICATION
	String OOB_REDIRECT_URI		= "urn:ietf:wg:oauth:2.0:oob";
	String LOGIN_REDIRECT_URI	= "/login";
	
	//ERROR CODEs
	String ERROR_SERVER_ERROR 	= "server_error";
	String ERROR_INVALID_REQUEST= "invalid_request";
	
	//Schemes
	enum SchemeType {
		Basic,	//Client Basic
		Bearer;	//Oauth2 Bearer
	}
	
	//type of response
	enum ResponseType {
		token,
		code
	}
	
	//type of grant
	enum GrantType {
		password,
		client_credentials,
		
		authorization_code,
		access_token,
		refresh_token
	}
	
	/**
	 * 
	 * @return requested client
	 */
	String getClientId();
	
	/**
	 * 
	 * @return client requested secret
	 */
	String getClientSecret();
	
	/**
	 * 
	 * @return URI to handle the authorization token
	 */
	String getRedirectURI();
	
	/**
	 * 
	 * @return intended response type
	 */
	String getResponseType();
	
	/**
	 * 
	 * @return space delimiter of scope of token, which service is asking for
	 */
	String getScope();
	
	/**
	 * 
	 * @return turn the state passing around
	 */
	String getState();
	
	/**
	 * 
	 * @return requested grant type
	 */
	String getGrantType();
	
	/**
	 * 
	 * @return authorization code if grant type is authorization_code
	 */
	String getCode();
	
	/**
	 * 
	 * @return requested username if grant type is password
	 */
	String getUsername();
	
	/**
	 * 
	 * @return requested password if grant type is password 
	 */
	String getPassword();
}
