package javacloud.framework.security.internal;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.security.AccessGrant;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.Authenticator;
import javacloud.framework.security.Credentials;
import javacloud.framework.security.InvalidCredentialsException;

/**
 * Assuming a chain of authenticators each handle the authenticate only if appropriate otherwise NULL will be return.
 * 
 * @author ho
 *
 */
public class AuthenticatorManager implements Authenticator {
	private static final Logger logger = Logger.getLogger(AuthenticatorManager.class.getName());
	private final List<Authenticator> authenticators;
	/**
	 * 
	 * @param authenticators
	 */
	public AuthenticatorManager(List<Authenticator> authenticators) {
		this.authenticators = authenticators;
	}
	
	/**
	 * Each authenticator will return NULL if not applicable so we keep going to the finish.
	 * 
	 */
	@Override
	public AccessGrant authenticate(Credentials credentials) throws AuthenticationException {
		for(Authenticator authenticator: authenticators) {
			AccessGrant authzGrant = authenticator.authenticate(credentials);
			if(authzGrant != null) {
				return authzGrant;
			}
		}
		
		//ASSMUING CREDENTIALS IS INVALID
		logger.log(Level.FINE, "Not found any authenticator for credentials type: {0}", credentials.getClass().getName());
		throw new InvalidCredentialsException();
	}
}
