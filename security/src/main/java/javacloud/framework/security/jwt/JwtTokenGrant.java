package javacloud.framework.security.jwt;

import java.util.Date;
import java.util.Map;

import javacloud.framework.security.IdParameters;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.util.Converters;

// custom token grant to expose claim
public final class JwtTokenGrant extends TokenGrant {
	private final JwtToken jwt;
	
	public JwtTokenGrant(String raw, JwtToken jwt) {
		super(raw, claimId(jwt), claimType(jwt));
		this.jwt = jwt;
	}
	
	@Override
	public String getName() {
		return jwt.getClaim(JwtToken.CLAIM_SUBJECT);
	}
	
	@Override
	public String getAudience() {
		return jwt.getClaim(JwtToken.CLAIM_AUDIENCE);
	}

	@Override
	public Date getExpireAt() {
		return new Date(Converters.LONG.apply(jwt.getClaim(JwtToken.CLAIM_EXPIRATION)));
	}

	@Override
	public Date getIssuedAt() {
		return new Date(Converters.LONG.apply(jwt.getClaim(JwtToken.CLAIM_ISSUEDAT)));
	}

	@Override
	public Map<String, Object> getClaims() {
		return jwt.getClaims();
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