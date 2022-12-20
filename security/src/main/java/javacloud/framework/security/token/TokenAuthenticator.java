package javacloud.framework.security.token;

import javacloud.framework.security.AccessGrant;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.Authenticator;
import javacloud.framework.security.Credentials;
import javacloud.framework.security.InvalidCredentialsException;
import javacloud.framework.security.internal.AuthorizationGrant;
import javacloud.framework.security.internal.Permissions;
import javacloud.framework.security.internal.TokenCredentials;
import javacloud.framework.util.Converters;
import javacloud.framework.util.Objects;

import java.util.Set;
/**
 * Keeping the JWT token within processing to be able to access other services on behalf of a principal if need.
 * 
 * @author ho
 *
 */
public class TokenAuthenticator implements Authenticator {
	private final TokenValidator tokenValidator;
	
	public TokenAuthenticator(TokenValidator tokenValidator) {
		this.tokenValidator = tokenValidator;
	}
	
	/**
	 * 
	 */
	@Override
	public AccessGrant authenticate(Credentials credentials) throws AuthenticationException {
		//NOT APPLICABLE
		if (!(credentials instanceof TokenCredentials)) {
			return null;
		}
		
		//VALIDATE TOKEN
		TokenCredentials tcredentials = (TokenCredentials)credentials;
		TokenGrant token = tokenValidator.validateToken(tcredentials.getToken());
		
		//TOKEN TYPE MUST MATCH!!!
		if (!token.getType().name().equals(tcredentials.getName())) {
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
		return new AuthorizationGrant(token, grantRoles(token))
						.withClaims(token.getClaims());
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	protected Set<String> grantRoles(TokenGrant token) {
		String roles = token.getRoles();
		if (!Objects.isEmpty(roles)) {
			return Objects.asSet(Converters.toArray(token.getRoles(), " ", true));
		}
		return Permissions.EMPTY_ROLES;
	}
}
