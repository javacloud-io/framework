package javacloud.framework.security.jwt;

import javacloud.framework.io.Externalizer;
import javacloud.framework.security.AccessGrant;
import javacloud.framework.security.IdParameters;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.security.token.TokenProvider;
import javacloud.framework.util.Converters;
import javacloud.framework.util.Objects;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Basic implementation of JTW token which is compatible with validator.
 * Make sure the signer is correctly configure.
 *  
 * @author ho
 *
 */
public abstract class JwtTokenProvider implements TokenProvider {
	private final JwtCodecs	jwtCodecs;
	private final JwtSigner	jwtSigner;
	
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
		String uuid   = UUID.randomUUID().toString();
		
		//Compose JWT TOKEN
		Map<String, Object> claims = Objects.asMap(
				JwtToken.CLAIM_ID,			uuid,
				JwtToken.CLAIM_TYPE, 		type.name(),
				JwtToken.CLAIM_ISSUER, 		jwtTokenIssuer(),
				JwtToken.CLAIM_SUBJECT, 	authzGrant.getName(),
				JwtToken.CLAIM_AUDIENCE, 	authzGrant.getAudience(),
				JwtToken.CLAIM_SCOPE, 		authzGrant.getScope(),
				JwtToken.CLAIM_ROLES, 		roles,
				JwtToken.CLAIM_ISSUEDAT, 	issuedAt.getTime(),
				JwtToken.CLAIM_EXPIRATION,	expireAt.getTime()
		);
		
		//TOKEN details
		TokenGrant token = new TokenGrant(jwtCodecs.encodeJWT(new JwtToken(jwtTokenType(), claims), jwtSigner),
				uuid, type, authzGrant.getName(), authzGrant.getAudience());
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
