package javacloud.framework.security.jwt;

import java.util.Map;

import javacloud.framework.util.Objects;

/**
 * Simple token representation with Dictionary claims set.
 * 
 * @author ho
 *
 */
public final class JwtToken {
	public static final String CLAIM_ISSUER 		= "iss";
	public static final String CLAIM_SUBJECT 		= "sub";
	public static final String CLAIM_AUDIENCE		= "aud";
	public static final String CLAIM_SCOPE 			= "scope";
	public static final String CLAIM_EXPIRATION 	= "exp";
	public static final String CLAIM_ISSUEDAT 		= "iat";
	
	//custom fields
	public static final String CLAIM_ID			= "jti";	//ID
	public static final String CLAIM_TYPE 		= "jtt";	//type
	public static final String CLAIM_ROLES 		= "roles";	//subject roles
	
	private String type;
	private String algorithm;
	private Map<String, Object> claims;
	/**
	 * 
	 * @param type
	 * @param algorithm
	 * @param claims
	 */
	public JwtToken(String type, String algorithm, Map<String, Object> claims) {
		this.type = type;
		this.algorithm = algorithm;
		this.claims = claims;
	}
	
	/**
	 * 
	 * @param type
	 * @param claims
	 */
	public JwtToken(String type, Map<String, Object> claims) {
		this.type = type;
		this.claims = claims;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAlgorithm() {
		return algorithm;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, Object> getClaims() {
		return claims;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 */
	public <T> T getClaim(String name) {
		return	Objects.cast(claims == null? null : claims.get(name));
	}
}
