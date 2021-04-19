package javacloud.framework.security.jwt;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import javacloud.framework.io.Externalizer;
import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.security.token.TokenValidator;
import javacloud.framework.util.Converters;

/**
 * Simple implementation of a JWT validation with a trusted signer, assuming sharing the signing key or public/private
 * 
 * @author ho
 *
 */
@Singleton
public class JwtTokenValidator implements TokenValidator {
	private final JwtCodecs  	jwtCodecs;
	private final JwtVerifier	jwtVerifier;
	
	/**
	 * 
	 * @param externalizer
	 * @param jwtVerifier
	 */
	@Inject
	public JwtTokenValidator(@Named(Externalizer.JSON) Externalizer externalizer, JwtVerifier jwtVerifier) {
		this.jwtCodecs = new JwtCodecs(externalizer);
		this.jwtVerifier  = jwtVerifier;
	}
	
	/**
	 * Validate token signature to make sure no tempering.
	 * 
	 */
	@Override
	public TokenGrant validateToken(String token) throws AuthenticationException {
		JwtToken jwt = jwtCodecs.decodeJWT(token, jwtVerifier);
		
		long expireAt = Converters.LONG.apply(jwt.getClaim(JwtToken.CLAIM_EXPIRATION));
		if (expireAt > System.currentTimeMillis()) {
			throw new AccessDeniedException(AccessDeniedException.EXPIRED_CREDENTIALS);
		}
		
		//PARSE TOKEN & MAKE SURE IT's STILL GOOD
		return new JwtTokenGrant(token, jwt);
	}
}
