package io.javacloud.framework.security.claim;

import io.javacloud.framework.data.Identifiable;
import io.javacloud.framework.security.IdParameters;

import java.security.Principal;
import java.util.Date;
/**
 * User access token, use on behalf of user credentials. System always lookup the user when such one is provided.
 * Just simply using type to enforce token used at the moment.
 * 
 * Trying to use JWT format with basic simple validation.
 * 
 * @author tobi
 *
 */
public class TokenGrant extends Identifiable<String> implements Principal {
	private IdParameters.GrantType 	type;		//type of token
	private	String 	subject;	//sub
	private String  audience;	//aud
	private String	scope;		//scope
	
	private String	roles;		//custom roles
	private	Date 	expireAt;	//exp UTC
	private	Date 	issuedAt;	//iat UTC
	public TokenGrant() {
	}
	
	/**
	 * 
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
	public void setType(IdParameters.GrantType type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
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
	 * 
	 */
	@Override
	public String toString() {
		return getId();
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
