package javacloud.framework.security.jwt;

import java.time.Instant;

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
public class JwtTokenValidator implements TokenValidator {
	private final JwtCodecs  	jwtCodecs;
	private final JwtVerifier.Supplier	jwtVerifier;
	
	/**
	 * 
	 * @param externalizer
	 * @param jwtVerifier
	 */
	public JwtTokenValidator(Externalizer externalizer, JwtVerifier.Supplier jwtVerifier) {
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
		
		// epoch milliseconds expireAt > now
		Instant expireAt = Instant.ofEpochSecond(Converters.LONG.apply(jwt.getClaim(JwtToken.CLAIM_EXPIRATION)));
		if (!expireAt.isAfter(Instant.now())) {
			throw new AccessDeniedException(AccessDeniedException.EXPIRED_CREDENTIALS);
		}
		
		// advance validation for iss/aud/scope
		validateClaims(jwt);
		
		//PARSE TOKEN & MAKE SURE IT's STILL GOOD
		return new JwtTokenGrant(token, jwt);
	}
	
	protected void validateClaims(JwtToken jwt) throws AuthenticationException {
	}
}
