package com.appe.framework.jwt;
/**
 * 
 * @author ho
 *
 */
public interface JwtVerifier {
	/**
	 * 
	 * @param content
	 * @param signature
	 * @return
	 */
	public boolean verify(String payload, String signature);
}
