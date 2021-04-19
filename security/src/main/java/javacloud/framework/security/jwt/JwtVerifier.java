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
	boolean verify(String payload, String signature);
}
