package com.appe.framework.security.util;
/**
 * 
 * @author ho
 *
 */
public final class JwtToken {
	private String type;
	private String algorithm;
	private byte[] payload;
	/**
	 * 
	 * @param type
	 * @param algorithm
	 * @param payload
	 */
	public JwtToken(String type, String algorithm, byte[] payload) {
		this.type = type;
		this.algorithm = algorithm;
		this.payload = payload;
	}
	
	/**
	 * 
	 * @param type
	 * @param payload
	 */
	public JwtToken(String type, byte[] payload) {
		this.type = type;
		this.payload = payload;
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
	public byte[] getPayload() {
		return payload;
	}
}
