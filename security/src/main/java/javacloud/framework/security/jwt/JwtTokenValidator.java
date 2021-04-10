package javacloud.framework.security.jwt;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import javacloud.framework.io.Externalizer;
import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.IdParameters;
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
	private JwtCodecs  	jwtCodecs;
	private JwtVerifier	jwtVerifier;
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
		JwtToken jwtToken = jwtCodecs.decodeJWT(token, jwtVerifier);
		
		//PARSE TOKEN & MAKE SURE IT's STILL GOOD
		TokenGrant grantToken = new TokenGrant(token,
				jwtToken.getClaim(JwtToken.CLAIM_ID),
				IdParameters.GrantType.valueOf(jwtToken.getClaim(JwtToken.CLAIM_TYPE)),
				jwtToken.getClaim(JwtToken.CLAIM_SUBJECT),
				jwtToken.getClaim(JwtToken.CLAIM_AUDIENCE));
		
		grantToken.setScope(jwtToken.getClaim(JwtToken.CLAIM_SCOPE));
		grantToken.setRoles(jwtToken.getClaim(JwtToken.CLAIM_ROLES));
		
		grantToken.setExpireAt(new Date(Converters.LONG.apply(jwtToken.getClaim(JwtToken.CLAIM_EXPIRATION))));
		grantToken.setIssuedAt(new Date(Converters.LONG.apply(jwtToken.getClaim(JwtToken.CLAIM_ISSUEDAT))));
		if(TokenGrant.isExpired(grantToken)) {
			throw new AccessDeniedException(AccessDeniedException.EXPIRED_CREDENTIALS);
		}
		return grantToken;
	}
}
