package javacloud.framework.security.jwt;

import javacloud.framework.io.Externalizer;
import javacloud.framework.security.AccessGrant;
import javacloud.framework.security.IdParameters;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.security.token.TokenProvider;
import javacloud.framework.util.Converters;
import javacloud.framework.util.Objects;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Basic implementation of JTW token which is compatible with validator.
 * Make sure the signer is correctly configure.
 *  
 * @author ho
 *
 */
public class JwtTokenProvider implements TokenProvider {
	private final String jwtType;
	private final JwtCodecs	jwtCodecs;
	private final JwtSigner.Supplier jwtSigner;
	
	protected JwtTokenProvider(Externalizer externalizer, JwtSigner.Supplier jwtSigner) {
		this("JWT", externalizer, jwtSigner);
	}
	
	/**
	 * 
	 * @param externalizer
	 * @param jwtSigner
	 */
	protected JwtTokenProvider(String jwtType, Externalizer externalizer, JwtSigner.Supplier jwtSigner) {
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
		Instant issuedAt = Instant.now();
		Instant expireAt = issuedAt.plusSeconds(jwtTtls(type));
		
		//Compose JWT TOKEN
		Map<String, Object> claims = jwtClaims(authzGrant);
		claims.put(JwtToken.CLAIM_TYPE, 		type.name());
		claims.put(JwtToken.CLAIM_ISSUEDAT, 	issuedAt.getEpochSecond());
		claims.put(JwtToken.CLAIM_EXPIRATION,	expireAt.getEpochSecond());
		
		//TOKEN details
		JwtToken jwt = new JwtToken(jwtType, claims);
		return new JwtTokenGrant(jwtCodecs.encodeJWT(jwt, jwtSigner), jwt);
	}
	
	/**
	 * 
	 * @param authzGrant
	 * @return claims for current grant
	 */
	protected Map<String, Object> jwtClaims(AccessGrant authzGrant) {
		Map<String, Object> claims = authzGrant.getClaims();
		if (claims == null) {
			claims = new HashMap<>();
		} else {
			claims = new HashMap<>(claims);
		}
		
		// default roles
		Set<String> roles = authzGrant.getRoles();
		if (!Objects.isEmpty(roles)) {
			claims.put("roles", Converters.toString(" ", roles));
		}
		claims.put(JwtToken.CLAIM_ID, 		UUID.randomUUID().toString());
		claims.put(JwtToken.CLAIM_SUBJECT, 	authzGrant.getName());
		//claims.put(JwtToken.CLAIM_ISSUER,   authzGrant);
		//claims.put(JwtToken.CLAIM_AUDIENCE, authzGrant);
		//claims.put(JwtToken.CLAIM_SCOPE, 	  authzGrant);
		return claims;
	}
	
	/**
	 * 
	 * @param type
	 * @return token TTL in seconds
	 */
	protected int jwtTtls(IdParameters.GrantType type) {
		if (type == IdParameters.GrantType.authorization_code) {
			return 10 * 60;
		} else if (type == IdParameters.GrantType.refresh_token) {
			return 24 * 60 * 60;
		}
		return 3 * 60 * 60;
	}
}
