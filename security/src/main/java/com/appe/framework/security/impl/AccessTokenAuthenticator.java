package com.appe.framework.security.impl;

import javax.inject.Singleton;

import com.appe.framework.security.AccessDeniedException;
import com.appe.framework.security.AccessGrant;
import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.IdParameters;
import com.appe.framework.security.claim.TokenAuthenticator;
import com.appe.framework.security.claim.TokenGrant;

/**
 * Only allows the access token, use at application level where other token is NOT ALLOWS.
 * 
 * @author ho
 *
 */
@Singleton
public class AccessTokenAuthenticator extends TokenAuthenticator {
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
