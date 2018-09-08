package io.javacloud.framework.json;
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
	public boolean verify(String payload, String signature);
}
