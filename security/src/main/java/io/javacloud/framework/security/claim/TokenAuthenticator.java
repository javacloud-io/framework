package io.javacloud.framework.security.claim;

import io.javacloud.framework.data.Converters;
import io.javacloud.framework.security.AccessGrant;
import io.javacloud.framework.security.AuthenticationException;
import io.javacloud.framework.security.Authenticator;
import io.javacloud.framework.security.Credentials;
import io.javacloud.framework.security.InvalidCredentialsException;
import io.javacloud.framework.security.claim.TokenGrant;
import io.javacloud.framework.security.internal.AuthorizationGrant;
import io.javacloud.framework.security.internal.Permissions;
import io.javacloud.framework.security.internal.TokenCredentials;
import io.javacloud.framework.util.Objects;

import java.util.Set;
/**
 * Keeping the JWT token within processing to be able to access other services on behalf of a principal if need.
 * 
 * @author ho
 *
 */
public class TokenAuthenticator implements Authenticator {
	private TokenValidator tokenValidator;
	public TokenAuthenticator(TokenValidator tokenValidator) {
		this.tokenValidator = tokenValidator;
	}
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
			roles = Objects.asSet(Converters.toArray(token.getRoles(), " ", true));
		}
		
		//SIMPLE GRANT TO CARRY SCOPE
		return new AuthorizationGrant(token, roles)
						.withAudience(token.getAudience())
						.withScope(token.getScope());
	}
}
