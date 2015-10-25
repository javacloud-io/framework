/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

import com.appe.AppeException;
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
