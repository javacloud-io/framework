package com.appe.framework.security.util;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import com.appe.framework.util.Codecs;
import com.appe.framework.util.Hmacs;

/**
 * Take a token content and sign with the correct ALGORITHM. N
 * 
 * @author ho
 *
 */
public class JwtSigner {
	public static final String ALG_NONE  = "NONE";
	public static final String ALG_HS256 = "HS256";
	/**
	 * return algorithm NAME
	 * 
	 * @return
	 */
	public String getAlgorithm() {
		return ALG_NONE;
	}
	
	/**
	 * return base64 URL encoded string
	 * 
	 * @param content
	 * @return
	 */
	public String sign(byte[] content) {
		return Codecs.encodeBase64(content, true);
	}
	
	/**
	 *	HMAC SHA-256
	 */
	public final static class HS256 extends JwtSigner {
		private Key secret;
		public HS256(byte[] secret) {
			this.secret = new SecretKeySpec(secret, Hmacs.HmacSHA2);
		}
		
		/**
		 * 
		 */
		@Override
		public String getAlgorithm() {
			return ALG_HS256;
		}

		@Override
		public String sign(byte[] content) {
			return Codecs.encodeBase64(Hmacs.digest(secret, content), true);
		}
	}
}
