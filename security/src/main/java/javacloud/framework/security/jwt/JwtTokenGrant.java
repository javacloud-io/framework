package javacloud.framework.security.jwt;

import java.util.Date;

import javacloud.framework.security.IdParameters;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.util.Converters;

// custom token grant to expose claim
final class JwtTokenGrant extends TokenGrant {
	private final JwtToken jwt;
	
	JwtTokenGrant(String raw, JwtToken jwt) {
		super(raw, jwt.getClaim(JwtToken.CLAIM_ID),
				   IdParameters.GrantType.valueOf(jwt.getClaim(JwtToken.CLAIM_TYPE)),
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
}