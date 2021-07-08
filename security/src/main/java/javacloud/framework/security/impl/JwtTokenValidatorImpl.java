package javacloud.framework.security.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import javacloud.framework.io.Externalizer;
import javacloud.framework.security.jwt.JwtTokenValidator;
import javacloud.framework.security.jwt.JwtVerifier;

@Singleton
public class JwtTokenValidatorImpl extends JwtTokenValidator {
	
	@Inject
	public JwtTokenValidatorImpl(@Named(Externalizer.JSON) Externalizer externalizer, JwtVerifier jwtVerifier) {
		super(externalizer, jwtVerifier);
	}
}
