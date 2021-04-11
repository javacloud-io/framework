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
public abstract class TokenGrant implements Principal {
	private final String  raw;	// raw token
	private final String  id;	// token ID
	
	// type of token
	private final IdParameters.GrantType type;
	private	final String subject;	// sub
	private final String audience;	// aud
	
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
	public abstract String getScope();
	
	/**
	 * 
	 * @return
	 */
	public abstract String getRoles();
	
	/**
	 * 
	 * @return
	 */
	public abstract Date getExpireAt();
	
	/**
	 * 
	 * @return
	 */
	public abstract Date getIssuedAt();
	
	/**
	 * 
	 * @param <T>
	 * @param name
	 * @return custom claim
	 */
	public abstract <T> T getClaim(String name);
	
	/**
	 * return token id encoded.
	 */
	@Override
	public String toString() {
		return raw;
	}
}
