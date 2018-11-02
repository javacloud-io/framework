package io.javacloud.framework.security.jwt;

import io.javacloud.framework.json.impl.JacksonMapper;
import io.javacloud.framework.util.Codecs;
import io.javacloud.framework.util.Objects;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class JwtTokenTest extends TestCase {
	/**
	 * 
	 */
	public void testHS256() {
		JwtCodecs.HS256 hs256 = new JwtCodecs.HS256("a secret key".getBytes());
		doTest(hs256, hs256);
	}
	
	/**
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	public void testRS256() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
	    KeyPair keyPair = keyGen.genKeyPair();
	    
	    System.out.println("----PUBLIC KEY----");
	    System.out.println(Codecs.encodePEM(keyPair.getPublic().getEncoded()));
	    
	    System.out.println("----PRIVATE KEY----");
	    System.out.println(Codecs.encodePEM(keyPair.getPrivate().getEncoded()));
	    
		JwtCodecs.RS256S signer   = new JwtCodecs.RS256S(keyPair.getPrivate());
		JwtCodecs.RS256V verifier = new JwtCodecs.RS256V(keyPair.getPublic());
		doTest(signer, verifier);
	}
	
	/**
	 * 
	 * @param jwtSigner
	 * @param jwtVerifier
	 */
	private void doTest(JwtSigner jwtSigner, JwtVerifier jwtVerifier) {
		JwtCodecs jwtCodecs = new JwtCodecs(new JacksonMapper());
		JwtToken token = new JwtToken("JWT", Objects.asMap("a", "a", "b", "b"));
		String stoken = jwtCodecs.encodeJWT(token, jwtSigner);
		
		System.out.println(jwtSigner.getAlgorithm() + " JWT: " + stoken);
		JwtToken tt = jwtCodecs.decodeJWT(stoken, jwtVerifier);
		assertEquals(token.getType(), tt.getType());
		assertEquals(jwtSigner.getAlgorithm(), tt.getAlgorithm());
	}
}
