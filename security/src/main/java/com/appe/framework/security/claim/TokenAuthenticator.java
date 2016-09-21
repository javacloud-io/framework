package com.appe.framework.security.claim;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.Authenticator;
import com.appe.framework.security.AccessGrant;
import com.appe.framework.security.Credentials;
import com.appe.framework.security.InvalidCredentialsException;
import com.appe.framework.security.claim.TokenGrant;
import com.appe.framework.security.internal.AuthorizationGrant;
import com.appe.framework.security.internal.Permissions;
import com.appe.framework.security.internal.TokenCredentials;
import com.appe.framework.util.Objects;
/**
 * Keeping the JWT token within processing to be able to access other services on behalf of a principal if need.
 * 
 * @author ho
 *
 */
@Singleton
public class TokenAuthenticator implements Authenticator {
	@Inject
	private TokenValidator tokenValidator;
	
	/**
	 * 
	 */
	@Override
	public AccessGrant authenticate(Credentials credentials) throws AuthenticationException {
		//NOT APPLICABLE
		if(!(credentials instanceof TokenCredentials)) {
			return null;
		}
		
		//VALIDATE TOKEN
		TokenCredentials tcredentials = (TokenCredentials)credentials;
		TokenGrant token = tokenValidator.validateToken(tcredentials.getToken());
		
		//TOKEN TYPE MUST MATCH!!!
		if(!token.getType().name().equals(tcredentials.getName())) {
			throw new InvalidCredentialsException();
		}
		return grantAccess(token);
	}
	
	/**
	 * Make sure grant the access using correct audience & scope. Plus only give out ROLEs
	 * 
	 * @param token
	 * @return
	 * @throws AuthenticationException
	 */
	protected AccessGrant grantAccess(TokenGrant token) throws AuthenticationException {
		Set<String> roles;
		
		//PASSING ALONE ROLES
		if(Objects.isEmpty(token.getRoles())) {
			roles = Permissions.EMPTY_ROLES;
		} else {
			roles = Objects.asSet(Objects.toArray(token.getRoles(), " ", true));
		}
		
		//SIMPLE GRANT TO CARRY SCOPE
		return new AuthorizationGrant(token, roles)
						.withAudience(token.getAudience())
						.withScope(token.getScope());
	}
}
