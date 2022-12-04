package javacloud.framework.security.jwt;

import java.util.Date;

import javacloud.framework.security.IdParameters;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.util.Converters;

// custom token grant to expose claim
public final class JwtTokenGrant extends TokenGrant {
	private final JwtToken jwt;
	
	JwtTokenGrant(String raw, JwtToken jwt) {
		super(raw, claimId(jwt), claimType(jwt),
				   jwt.getClaim(JwtToken.CLAIM_SUBJECT),
				   jwt.getClaim(JwtToken.CLAIM_AUDIENCE));
		this.jwt = jwt;
	}
	
	@Override
	public <T> T getClaim(String name) {
		return jwt.getClaim(name);
	}

	@Override
	public String getScope() {
		return jwt.getClaim(JwtToken.CLAIM_SCOPE);
	}

	@Override
	public String getRoles() {
		return jwt.getClaim(JwtToken.CLAIM_ROLES);
	}

	@Override
	public Date getExpireAt() {
		return new Date(Converters.LONG.apply(jwt.getClaim(JwtToken.CLAIM_EXPIRATION)));
	}

	@Override
	public Date getIssuedAt() {
		return new Date(Converters.LONG.apply(jwt.getClaim(JwtToken.CLAIM_ISSUEDAT)));
	}
	
	/**
	 * 
	 * @return
	 */
	static String claimId(JwtToken jwt) {
		String id = jwt.getClaim(JwtToken.CLAIM_ID);
		if (id == null) {
			id = jwt.getClaim("tid");	//azure
		}
		return id;
	}
	
	/**
	 * 
	 * @param jwt
	 * @return
	 */
	static IdParameters.GrantType claimType(JwtToken jwt) {
		String type = jwt.getClaim(JwtToken.CLAIM_TYPE);
		if (type == null) {
			return IdParameters.GrantType.access_token;
		}
		return IdParameters.GrantType.valueOf(type);
	}
}