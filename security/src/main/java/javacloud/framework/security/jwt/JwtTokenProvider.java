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
	private final String jwtType;
	private final JwtCodecs	jwtCodecs;
	private final JwtSigner	jwtSigner;
	
	protected JwtTokenProvider(Externalizer externalizer, JwtSigner jwtSigner) {
		this("JWT", externalizer, jwtSigner);
	}
	
	/**
	 * 
	 * @param externalizer
	 * @param jwtSigner
	 */
	protected JwtTokenProvider(String jwtType, Externalizer externalizer, JwtSigner jwtSigner) {
		this.jwtType = jwtType;
		this.jwtCodecs = new JwtCodecs(externalizer);
		this.jwtSigner  = jwtSigner;
	}
	
	/**
	 * Compose a JWT token for given GRANT. Might need to expose more information about the GRANT.
	 */
	@Override
	public TokenGrant issueToken(AccessGrant authzGrant, IdParameters.GrantType type) {
		//EXPIRATION
		Date issuedAt = new Date();
		Date expireAt = new Date(issuedAt.getTime() + jwtTtls(type) * 1000L);
		
		//Compose JWT TOKEN
		Map<String, Object> claims = jwtClaims(authzGrant);
		claims.put(JwtToken.CLAIM_TYPE, 		type.name());
		claims.put(JwtToken.CLAIM_ISSUEDAT, 	issuedAt.getTime());
		claims.put(JwtToken.CLAIM_EXPIRATION,	expireAt.getTime());
		
		//TOKEN details
		JwtToken jwt = new JwtToken(jwtType, claims);
		return new JwtTokenGrant(jwtCodecs.encodeJWT(jwt, jwtSigner), jwt);
	}
	
	/**
	 * 
	 * @param authzGrant
	 * @return
	 */
	protected Map<String, Object> jwtClaims(AccessGrant authzGrant) {
		//SCOPE/ROLES
		String roles = null;
		if(!Objects.isEmpty(authzGrant.getRoles())) {
			roles = Converters.toString(" ", authzGrant.getRoles().toArray());
		}
		return	Objects.asMap(
					JwtToken.CLAIM_ID,			UUID.randomUUID().toString(),
					JwtToken.CLAIM_ISSUER, 		jwtIssuer(authzGrant),
					JwtToken.CLAIM_SUBJECT, 	authzGrant.getName(),
					JwtToken.CLAIM_AUDIENCE, 	authzGrant.getAudience(),
					JwtToken.CLAIM_SCOPE, 		authzGrant.getScope(),
					JwtToken.CLAIM_ROLES, 		roles
				);
	}
	
	/**
	 * 
	 * @return the actual issuer/null
	 */
	protected abstract String jwtIssuer(AccessGrant authzGrant);
	
	/**
	 * 
	 * @param type
	 * @return token TTL in seconds
	 */
	protected abstract int jwtTtls(IdParameters.GrantType type);
}
