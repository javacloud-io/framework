package com.appe.framework.security.jwt;

import java.util.Date;

import com.appe.framework.json.Externalizer;
import com.appe.framework.jwt.JwtCodecs;
import com.appe.framework.jwt.JwtSigner;
import com.appe.framework.jwt.JwtToken;
import com.appe.framework.security.AccessGrant;
import com.appe.framework.security.IdParameters;
import com.appe.framework.security.claim.TokenGrant;
import com.appe.framework.security.claim.TokenProvider;
import com.appe.framework.util.Dictionary;
import com.appe.framework.util.Objects;

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
			token.setRoles(Objects.toString(" ", authzGrant.getRoles().toArray()));
		}
		
		//EXPIRATION
		token.setIssuedAt(new Date());
		int ttls = jwtTokenTTL(type);
		token.setExpireAt(new java.util.Date(token.getIssuedAt().getTime() + ttls * 1000L));
		
		//Compose JWT TOKEN
		Dictionary claims = Objects.asDict(
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
