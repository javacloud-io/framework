package javacloud.framework.security.jwt;

import javacloud.framework.json.impl.JacksonMapper;
import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AccessGrant;
import javacloud.framework.security.IdParameters.GrantType;
import javacloud.framework.security.internal.Permissions;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Objects;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

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
		doTestTTLS(hs256, hs256, 10);
		
		try {
			doTestTTLS(hs256, hs256, 0);
		} catch (AccessDeniedException ex) {
			Assert.assertEquals(AccessDeniedException.EXPIRED_CREDENTIALS, ex.getMessage());
		}
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
	    System.out.println(Codecs.Base64Encoder.apply(keyPair.getPublic().getEncoded(), false, true));
	    
	    System.out.println("----PRIVATE KEY----");
	    System.out.println(Codecs.Base64Encoder.apply(keyPair.getPrivate().getEncoded(), false, true));
	    
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
		String stoken = jwtCodecs.encodeJWT(token, () -> jwtSigner);
		
		System.out.println(jwtSigner.algorithm() + " JWT: " + stoken);
		JwtToken tt = jwtCodecs.decodeJWT(stoken, (kid) -> jwtVerifier);
		assertEquals(token.getType(), tt.getType());
		assertEquals(jwtSigner.algorithm(), tt.getAlgorithm());
	}
	
	private void doTestTTLS(JwtSigner jwtSigner, JwtVerifier jwtVerifier, final int ttls) {
		JacksonMapper mapper = new JacksonMapper();
		JwtTokenProvider provider = new JwtTokenProvider(mapper, () -> jwtSigner) {
			@Override
			protected int jwtTtls(GrantType type) {
				return ttls;
			}
		};
		JwtTokenValidator validator  = new JwtTokenValidator(mapper, (__) -> jwtVerifier);
		
		TokenGrant token = provider.issueToken(new SimpleGrant(), GrantType.access_token);
		validator.validateToken(token.toString());
	}
	
	static class SimpleGrant implements AccessGrant {
		@Override
		public String getName() {
			return "test";
		}

		@Override
		public Principal getSubject() {
			return this;
		}

		@Override
		public Set<String> getRoles() {
			return Permissions.EMPTY_ROLES;
		}

		@Override
		public Map<String, Object> getClaims() {
			return Map.of("a", "a", "b", "b");
		}
	}
}
