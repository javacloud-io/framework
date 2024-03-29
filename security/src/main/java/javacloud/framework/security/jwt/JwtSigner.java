package javacloud.framework.security.jwt;

/**
 * Take a token content and sign with the correct ALGORITHM. N
 * 
 * @author ho
 *
 */
public interface JwtSigner {
	String ALG_NONE  = "none";
	String ALG_HS256 = "HS256";
	String ALG_RS256 = "RS256";
	
	/**
	 * 
	 * @return algorithm NAME
	 */
	String algorithm();
	
	/**
	 * 
	 * @return keyId
	 */
	default String keyId() {
		return null;
	}
	
	/**
	 * 
	 * @param content
	 * @return base64 URL encoded string
	 */
	byte[] sign(byte[] payload);
	
	
	interface Supplier {
		JwtSigner get();
	}
}
