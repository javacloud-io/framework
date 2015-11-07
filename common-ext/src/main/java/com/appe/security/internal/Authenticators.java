package com.appe.security.internal;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.security.Authorization;
import com.appe.security.AuthenticationException;
import com.appe.security.Authenticator;
import com.appe.security.InvalidCredentialsException;
import com.appe.util.Objects;

/**
 * Assuming a chain of authenticators each handle the authenticate only if appropriate otherwise NULL will be return.
 * 
 * @author ho
 *
 */
public class Authenticators implements Authenticator {
	private static final Logger logger = LoggerFactory.getLogger(Authenticators.class);
	
	private final List<Authenticator> authenticators;
	/**
	 * 
	 * @param authenticators
	 */
	public Authenticators(List<Authenticator> authenticators) {
		this.authenticators = authenticators;
	}
	/**
	 * 
	 * @param authenticator
	 */
	public Authenticators(Authenticator authenticator) {
		this.authenticators = Objects.asList(authenticator);
	}
	
	/**
	 * Each authenticator will return NULL if not applicable so we keep going to the finish.
	 * 
	 */
	@Override
	public Authorization authenticate(Principal credentials) throws AuthenticationException {
		for(Authenticator authenticator: authenticators) {
			Authorization authzGrant = authenticator.authenticate(credentials);
			if(authzGrant != null) {
				return authzGrant;
			}
		}
		
		//ASSMUING CREDENTIALS IS INVALID
		logger.debug("Not found any authenticator for credentials class: " + credentials.getClass().getName());
		throw new InvalidCredentialsException();
	}
}
