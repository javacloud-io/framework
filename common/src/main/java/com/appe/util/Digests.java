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

import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.NoSuchAlgorithmException;

import com.appe.AppeException;

/**
 * Thread safe message DIGEST. NEED TO CALL RESET before using any of these
 * MD5, SHA-256, SHA-1...
 * 
 * Perfectly safe if doing reset(), update() and digest() on the same thread.
 * @author aimee
 *
 */
public final class Digests {
	public static final String MD5 = "MD5";
	public static final String SHA1= "SHA-1";
	public static final String SHA2= "SHA-256";
	
	private Digests() {
	}
	
	/**
	 * QUICK UTILS to md5 hash value
	 * @param bytes
	 * 
	 * @return
	 */
	public static byte[] md5(byte[] ...bytes) {
		return digest(MD5, bytes);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param ofs
	 * @param len
	 * @return
	 */
	public static byte[] md5(byte[] bytes, int ofs, int len) {
		return digest(MD5, bytes, ofs, len);
	}
	
	/**
	 * QUICK UTILS to sha1 hash value
	 * @param bytes
	 * @return
	 */
	public static byte[] sha1(byte[] ...bytes) {
		return digest(SHA1, bytes);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param ofs
	 * @param len
	 * @return
	 */
	public static byte[] sha1(byte[] bytes, int ofs, int len) {
		return digest(SHA1, bytes, ofs, len);
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] sha2(byte[] ...bytes) {
		return digest(SHA2, bytes);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param ofs
	 * @param len
	 * @return
	 */
	public static byte[] sha2(byte[] bytes, int ofs, int len) {
		return digest(SHA2, bytes, ofs, len);
	}
	
	/**
	 * This is a safe digest, always SAFE, very useful when performing simple operation.  
	 * @param algorithm
	 * @param bytes
	 * @return
	 */
	public static byte[] digest(String algorithm, byte[] ...bytes) {
		java.security.MessageDigest md = get(algorithm);
		for(byte[] bb: bytes) {
			md.update(bb);
		}
		return md.digest();
	}
	
	/**
	 * Digest only portion of the bytes
	 * 
	 * @param algorithm
	 * @param bytes
	 * @param ofs
	 * @param len
	 * @return
	 */
	public static byte[] digest(String algorithm, byte[] bytes, int ofs, int len) {
		java.security.MessageDigest md = get(algorithm);
		md.update(bytes, ofs, len);
		return md.digest();
	}
	
	/**
	 * Create digest input stream
	 * @param algorithm
	 * @param stream
	 * @return
	 */
	public DigestInputStream digest(String algorithm, InputStream stream) {
		java.security.MessageDigest md = get(algorithm);
		return new DigestInputStream(stream, md);
	}
	
	/**
	 * 
	 * @param algorithm
	 * @param stream
	 * @return
	 */
	public DigestOutputStream digest(String algorithm, OutputStream stream) {
		java.security.MessageDigest md = get(algorithm);
		return new DigestOutputStream(stream, md);
	}
	
	/**
	 * Unchecked way to allocate the instance.
	 * @param algorithm
	 * @return
	 */
	public static java.security.MessageDigest get(String algorithm) {
		try {
			return	java.security.MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw AppeException.wrap(ex);
		}
	}
}
