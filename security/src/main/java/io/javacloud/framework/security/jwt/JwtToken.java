package io.javacloud.framework.security.jwt;

import io.javacloud.framework.util.Dictionary;

/**
 * Simple token representation with Dictionary claims set.
 * 
 * @author ho
 *
 */
public final class JwtToken {
	private String type;
	private String algorithm;
	private Dictionary claims;
	/**
	 * 
	 * @param type
	 * @param algorithm
	 * @param claims
	 */
	public JwtToken(String type, String algorithm, Dictionary claims) {
		this.type = type;
		this.algorithm = algorithm;
		this.claims = claims;
	}
	
	/**
	 * 
	 * @param type
	 * @param claims
	 */
	public JwtToken(String type, Dictionary claims) {
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
	public Dictionary getClaims() {
		return claims;
	}
}
