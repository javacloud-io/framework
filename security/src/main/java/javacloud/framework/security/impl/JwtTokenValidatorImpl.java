package javacloud.framework.security.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import javacloud.framework.io.Externalizer;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.InvalidCredentialsException;
import javacloud.framework.security.jwt.JwtToken;
import javacloud.framework.security.jwt.JwtTokenValidator;
import javacloud.framework.security.jwt.JwtVerifier;

@Singleton
public class JwtTokenValidatorImpl extends JwtTokenValidator {
	
	@Inject
	public JwtTokenValidatorImpl(@Named(Externalizer.JSON) Externalizer externalizer, JwtVerifier.Supplier jwtVerifier) {
		super(externalizer, jwtVerifier);
	}

	@Override
	protected void validateScope(JwtToken jwt) throws AuthenticationException {
		String issuer = jwt.getClaim(JwtToken.CLAIM_ISSUER);
		// application id
		if (!"https://sts.windows.net/57129b30-4f50-46c5-8699-6f303db27673/".equals(issuer)) {
			throw new InvalidCredentialsException("invalid_issuer");
		}
		
		// register name 
		String aud = jwt.getClaim(JwtToken.CLAIM_AUDIENCE);
		if (!"api://8e03ba37-0373-459f-8cfe-17108b077ea0".equals(aud)) {
			throw new InvalidCredentialsException("invalid_aud");
		}
		super.validateScope(jwt);
	}
}
