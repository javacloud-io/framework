package javacloud.framework.security.impl;

import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AccessGrant;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.IdParameters;
import javacloud.framework.security.token.TokenAuthenticator;
import javacloud.framework.security.token.TokenGrant;
import javacloud.framework.security.token.TokenValidator;

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
