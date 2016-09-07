package com.appe.framework.misc;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

import com.appe.framework.AppeException;
/**
 * 
 * @author tobi
 *
 */
public final class Hmacs {
	public static final String HmacSHA1 = "HmacSHA1";
	public static final String HmacSHA2 = "HmacSHA256";
	private Hmacs() {
	}
	
	/**
	 * SHA1 using HMAC algorithm with a known secret KEY.
	 * @param secret
	 * @param bytes
	 * @return
	 */
	public static byte[] sha1(byte[] secret, byte[]... bytes) {
		return digest(HmacSHA1, secret, bytes);
	}
	
	/**
	 * Digest bytes data using specified algorithm and secret.
	 * @param algorithm
	 * @param secret
	 * @param bytes
	 * @return
	 */
	public static byte[] digest(String algorithm, byte[] secret, byte[]... bytes) {
		return digest(new SecretKeySpec(secret, algorithm));
	}
	
	/**
	 * 
	 * @param secret
	 * @param bytes
	 * @return
	 */
	public static byte[] digest(Key secret, byte[]... bytes) {
		javax.crypto.Mac mac = get(secret);
		for(byte[] bb: bytes) {
			mac.update(bb);
		}
		return mac.doFinal();
	}
	
	/**
	 * return MAC and always initialize with KEY.
	 * @param secret
	 * @return
	 */
	public static javax.crypto.Mac get(Key secret) {
		try {
			javax.crypto.Mac hmac = javax.crypto.Mac.getInstance(secret.getAlgorithm());
			hmac.init(secret);
			return hmac;
		}catch(InvalidKeyException | NoSuchAlgorithmException ex) {
			throw AppeException.wrap(ex);
		}
	}
}
