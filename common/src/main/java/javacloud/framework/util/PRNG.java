package javacloud.framework.util;

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
	 * 
	 * @param len
	 * @return next bytes LENGTH
	 */
	public static byte[] nextBytes(int len) {
		byte[] bytes = new byte[len];
		SRAND.nextBytes(bytes);
		return bytes;
	}
	
	/**
	 * 
	 * @param len
	 * @return next big integer with number of bytes
	 */
	public static BigInteger nextBInteger(int len) {
		return new BigInteger(len << 3, SRAND);
	}
}
