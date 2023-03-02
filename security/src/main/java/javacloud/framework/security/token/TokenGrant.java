package javacloud.framework.security.token;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

import javacloud.framework.security.IdParameters;
import javacloud.framework.util.Objects;
/**
 * User access token, use on behalf of user credentials. System always lookup the user when such one is provided.
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
	
	public TokenGrant(String raw, String id, IdParameters.GrantType type) {
		this.raw = raw;
		this.id = id;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public IdParameters.GrantType getType() {
		return type;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 */
	public <T> T getClaim(String name) {
		Map<String, Object> claims = getClaims();
		return	Objects.cast(claims == null? null : claims.get(name));
	}
	
	// abstract claims
	public abstract String getAudience();
	public abstract Date getExpireAt();
	public abstract Date getIssuedAt();
	
	/**
	 * 
	 * @return custom claims
	 */
	public abstract Map<String, Object> getClaims();
	
	/**
	 * return token id encoded.
	 */
	@Override
	public String toString() {
		return raw;
	}
}
