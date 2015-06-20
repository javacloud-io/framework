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
package com.appe.security;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * It's OK to use just one PRNG for the whole JVM.
 * 
 * TODO: Should re-seed when get() PRNG?
 * 
 * @author tobi
 *
 */
public final class PRNG {
	//SHA1PRNG by default
	private static final SecureRandom SRAND = new SecureRandom();
	private PRNG() {
	}
	
	/**
	 * NOTES: Not need to be sure a singleton or anything!!!
	 * 
	 * @return
	 */
	public static SecureRandom get() {
		return SRAND;
	}
	
	/**
	 * return next bytes LENGTH
	 * 
	 * @param len
	 * @return
	 */
	public static byte[] nextBytes(int len) {
		byte[] bytes = new byte[len];
		SRAND.nextBytes(bytes);
		return bytes;
	}
	
	/**
	 * return next big integer with number of bytes
	 * 
	 * @param len
	 * @return
	 */
	public static BigInteger nextBInteger(int len) {
		return new BigInteger(len << 3, SRAND);
	}
}
