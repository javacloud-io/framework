package com.appe.framework.security.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.appe.framework.security.AccessDeniedException;
import com.appe.framework.security.AccessGrant;
import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.IdParameters;
import com.appe.framework.security.claim.TokenAuthenticator;
import com.appe.framework.security.claim.TokenGrant;
import com.appe.framework.security.claim.TokenValidator;

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