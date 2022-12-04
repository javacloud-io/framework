package javacloud.framework.security.jwt;
/**
 * Verify the signed payload with signature
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
	boolean verify(byte[] payload, byte[] signature);
	
	interface Supplier {
		JwtVerifier get(String kid);
	}
}
