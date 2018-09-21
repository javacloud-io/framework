package io.javacloud.framework.security.jwt;

import io.javacloud.framework.security.AccessGrant;
import io.javacloud.framework.security.IdParameters;
import io.javacloud.framework.security.claim.TokenGrant;
import io.javacloud.framework.security.claim.TokenProvider;
import io.javacloud.framework.util.Objects;

import java.util.Date;

import io.javacloud.framework.data.Converters;
import io.javacloud.framework.data.Dictionaries;
import io.javacloud.framework.data.Dictionary;
import io.javacloud.framework.data.Externalizer;
import io.javacloud.framework.json.JwtSigner;
import io.javacloud.framework.json.internal.JwtCodecs;
import io.javacloud.framework.json.internal.JwtToken;

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
		TokenGrant token = new TokenGrant();
		token.setType(type);
		token.setSubject(authzGrant.getName());
		token.setAudience(authzGrant.getAudience());
		
		//SCOPE/ROLES
		token.setScope(authzGrant.getScope());
		if(!Objects.isEmpty(authzGrant.getRoles())) {
			token.setRoles(Converters.toString(" ", authzGrant.getRoles().toArray()));
		}
		
		//EXPIRATION
		token.setIssuedAt(new Date());
		int ttls = jwtTokenTTL(type);
		token.setExpireAt(new java.util.Date(token.getIssuedAt().getTime() + ttls * 1000L));
		
		//Compose JWT TOKEN
		Dictionary claims = Dictionaries.asDict(
			JwtTokenValidator.JWT_TYPE, 		type.name(),
			JwtTokenValidator.JWT_ISSUER, 		jwtTokenIssuer(),
			JwtTokenValidator.JWT_SUBJECT, 		token.getSubject(),
			JwtTokenValidator.JWT_AUDIENCE, 	token.getAudience(),
			JwtTokenValidator.JWT_SCOPE, 		token.getScope(),
			JwtTokenValidator.JWT_ROLES, 		token.getRoles(),
			JwtTokenValidator.JWT_ISSUEDAT, 	token.getIssuedAt().getTime(),
			JwtTokenValidator.JWT_EXPIRATION,	token.getExpireAt().getTime()
		);
		
		token.setId(jwtCodecs.encodeJWT(new JwtToken(jwtTokenType(), claims), jwtSigner));
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
