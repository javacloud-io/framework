package javacloud.framework.security.jwt;

/**
 * Take a token content and sign with the correct ALGORITHM. N
 * 
 * @author ho
 *
 */
public interface JwtSigner {
	public static final String ALG_NONE  = "none";
	public static final String ALG_HS256 = "HS256";
	public static final String ALG_RS256 = "RS256";
	
	/**
	 * return algorithm NAME
	 * 
	 * @return
	 */
	public String getAlgorithm();
	
	/**
	 * return base64 URL encoded string
	 * 
	 * @param content
	 * @return
	 */
	public String sign(String payload);
}
