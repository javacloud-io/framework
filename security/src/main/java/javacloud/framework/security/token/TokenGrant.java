package javacloud.framework.security.token;

import java.security.Principal;
import java.util.Date;

import javacloud.framework.security.IdParameters;
/**
 * User access token, use on behalf of user credentials. System always lookup the user when such one is provided.
 * Just simply using type to enforce token used at the moment.
 * 
 * Trying to use JWT format with basic simple validation.
 * 
 * @author tobi
 *
 */
public class TokenGrant implements Principal {
	private final String  raw;		// raw token
	private final String  id;		//token ID
	
	private final IdParameters.GrantType 	type;		//type of token
	private	final String 	subject;	//sub
	private final String  audience;	//aud
	private String	scope;		//scope
	
	private String	roles;		//custom roles
	private	Date 	issuedAt;	//iat UTC
	private	Date 	expireAt;	//exp UTC
	
	public TokenGrant(String raw, String id, IdParameters.GrantType type, String subject, String audience) {
		this.raw= raw;
		this.id = id;
		this.type = type;
		this.subject = subject;
		this.audience= audience;
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * Name of the principle
	 */
	@Override
	public String getName() {
		return subject;
	}
	
	/**
	 * 
	 * @return
	 */
	public IdParameters.GrantType getType() {
		return type;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAudience() {
		return audience;
	}

	/**
	 * 
	 * @return
	 */
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRoles() {
		return roles;
	}
	
	public void setRoles(String roles) {
		this.roles = roles;
	}

	/**
	 * 
	 * @return
	 */
	public Date getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Date expireAt) {
		this.expireAt = expireAt;
	}
	
	/**
	 * 
	 * @return
	 */
	public Date getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Date issuedAt) {
		this.issuedAt = issuedAt;
	}
	
	/**
	 * return token id encoded.
	 */
	@Override
	public String toString() {
		return raw;
	}
	
	/**
	 * If expired if has expiry and too OLD.
	 * 
	 * @param token
	 * @return
	 */
	public static boolean isExpired(TokenGrant token) {
		return (token.expireAt != null
					&& token.expireAt.getTime() < System.currentTimeMillis());
	}
}
