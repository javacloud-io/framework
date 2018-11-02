package io.javacloud.framework.security.jwt;

import io.javacloud.framework.security.AccessGrant;
import io.javacloud.framework.security.IdParameters;
import io.javacloud.framework.security.claim.TokenGrant;
import io.javacloud.framework.security.claim.TokenProvider;
import io.javacloud.framework.util.Converters;
import io.javacloud.framework.util.Externalizer;
import io.javacloud.framework.util.Objects;

import java.util.Date;
import java.util.Map;

/**
 * Basic implementation of JTW token which is compatible with validator.
 * Make sure the signer is correctly configure.
 *  
 * @author ho
 *
 */
public abstract class JwtTokenProvider implements TokenProvider {
	private JwtCodecs	jwtCodecs;
	private JwtSigner	jwtSigner;
	/**
	 * 
	 * @param externalizer
	 * @param jwtSigner
	 */
	protected JwtTokenProvider(Externalizer externalizer, JwtSigner jwtSigner) {
		this.jwtCodecs = new JwtCodecs(externalizer);
		this.jwtSigner  = jwtSigner;
	}
	
	/**
	 * Compose a JWT token for given GRANT. Might need to expose more information about the GRANT.
	 */
	@Override
	public TokenGrant issueToken(AccessGrant authzGrant, IdParameters.GrantType type) {
		//SCOPE/ROLES
		String roles = null;
		if(!Objects.isEmpty(authzGrant.getRoles())) {
			roles = Converters.toString(" ", authzGrant.getRoles().toArray());
		}
		
		//EXPIRATION
		Date issuedAt = new Date();
		Date expireAt = new java.util.Date(issuedAt.getTime() + jwtTokenTTL(type) * 1000L);
		
		//Compose JWT TOKEN
		Map<String, Object> claims = Objects.asMap(
			JwtTokenValidator.JWT_TYPE, 		type.name(),
			JwtTokenValidator.JWT_ISSUER, 		jwtTokenIssuer(),
			JwtTokenValidator.JWT_SUBJECT, 		authzGrant.getName(),
			JwtTokenValidator.JWT_AUDIENCE, 	authzGrant.getAudience(),
			JwtTokenValidator.JWT_SCOPE, 		authzGrant.getScope(),
			JwtTokenValidator.JWT_ROLES, 		roles,
			JwtTokenValidator.JWT_ISSUEDAT, 	issuedAt.getTime(),
			JwtTokenValidator.JWT_EXPIRATION,	expireAt.getTime()
		);
		
		//TOKEN
		TokenGrant token = new TokenGrant(jwtCodecs.encodeJWT(new JwtToken(jwtTokenType(), claims), jwtSigner),
				type,
				authzGrant.getName(),
				authzGrant.getAudience());
		token.setScope(authzGrant.getScope());
		token.setRoles(roles);
		token.setIssuedAt(issuedAt);
		token.setExpireAt(expireAt);
		return token;
	}
	
	/**
	 * return token TTL in seconds
	 * 
	 * @param type
	 * @return
	 */
	protected abstract int jwtTokenTTL(IdParameters.GrantType type);
	
	/**
	 * return the actual issuer
	 * 
	 * @return
	 */
	protected abstract String jwtTokenIssuer();
	
	/**
	 * 
	 * @return
	 */
	protected abstract String jwtTokenType();
}
