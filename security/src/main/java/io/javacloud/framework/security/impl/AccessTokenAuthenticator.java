package io.javacloud.framework.security.impl;

import io.javacloud.framework.security.AccessDeniedException;
import io.javacloud.framework.security.AccessGrant;
import io.javacloud.framework.security.AuthenticationException;
import io.javacloud.framework.security.IdParameters;
import io.javacloud.framework.security.claim.TokenAuthenticator;
import io.javacloud.framework.security.claim.TokenGrant;
import io.javacloud.framework.security.claim.TokenValidator;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Only allows the access token, use at application level where other token is NOT ALLOWS.
 * 
 * @author ho
 *
 */
@Singleton
public class AccessTokenAuthenticator extends TokenAuthenticator {
	@Inject
	public AccessTokenAuthenticator(TokenValidator tokenValidator) {
		super(tokenValidator);
	}

	/**
	 * 
	 */
	@Override
	protected AccessGrant grantAccess(TokenGrant token) throws AuthenticationException {
		if(token.getType() != IdParameters.GrantType.access_token) {
			throw new AccessDeniedException();
		}
		return super.grantAccess(token);
	}
}
