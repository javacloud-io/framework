package com.appe.framework.server.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.appe.framework.AppeRegistry;
import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.Authenticator;
import com.appe.framework.security.AccessGrant;
import com.appe.framework.security.IdParameters;
import com.appe.framework.security.internal.AuthenticatorManager;
import com.appe.framework.security.internal.ClientCredentials;
import com.appe.framework.security.internal.TokenCredentials;
import com.appe.framework.server.internal.RequestWrapper;
import com.appe.framework.util.Objects;

/**
 * Simply process the authentication if any provided => authenticate and inject context if SUCCESS.
 * We are looking at <allow-cookie> and an <authenticator> parameters to indicate how the authorization process taking place.
 * 
 * <init-param>
 *	<param-name>authenticator</param-name>
 *	<param-value>list of binding names</param-value>		
 * </init-param>
 * <init-param>
 *	<param-name>allow-cookie</param-name>
 *	<param-value></param-value>		
 * </init-param>
 * 
 * Resources such as REST APIs is perfect for this usage! where at the resource level already handle the authorization.
 * 
 * @author ho
 *
 */
public class SecurityContextFilter extends ServletFilter {
	protected boolean 	allowsCookie;		//allows access cookie
	protected Authenticator authenticator;	//an authenticator
	public SecurityContextFilter() {
	}
	
	/**
	 * 
	 * @param filterConfig
	 * @throws ServletException
	 */
	@Override
	protected void init() throws ServletException {
		//OPTIONALS TOKEN PARAM
		this.allowsCookie= Boolean.valueOf(getInitParameter("allow-cookie"));
		
		//AUTHENTICATORs
		List<Authenticator> authenticators = new ArrayList<>();
		String authenticator = getInitParameter("authenticator");
		if(Objects.isEmpty(authenticator)) {
			authenticators.add(AppeRegistry.get().getInstance(Authenticator.class));
		} else {
			for(String name: Objects.toArray(authenticator, ",", true)) {
				authenticators.add(AppeRegistry.get().getInstance(Authenticator.class, name));
			}
		}
		this.authenticator = new AuthenticatorManager(authenticators);
	}
	
	/**
	 * ONLY INJECT THE SECURITY CONTEXT IF GRANTED! ANY EXCEPTION WILL ASSUMING NO GRANT.
	 * 
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		try {
			AccessGrant authzGrant = doAuthenticate(req);
			if(authzGrant != null) {
				chain.doFilter(RequestWrapper.wrap(req, authzGrant), resp);
			} else {
				chain.doFilter(req, resp);
			}
		} catch(AuthenticationException ex) {
			logger.warn("Unauthorized credentials, reason: {}", ex.getMessage());
			chain.doFilter(req, resp);
		}
	}
	
	/**
	 * Make sure to add enough context around the authentication...
	 * ALSO CHECK FOR CONTEXT & PERMISSION....
	 *  
	 * @param req
	 * @return GRANTED principal if success.
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	protected AccessGrant doAuthenticate(HttpServletRequest req) throws ServletException, IOException, AuthenticationException {
		Principal credentials = requestCredentials(req);
		
		//ASSUMING NULL GRANT
		if(credentials == null) {
			return null;
		}
		
		//TODO: OK, FILL IN SOME REQUEST DETALS (IP, USER AGENT, DEVICE...)
		return	authenticator.authenticate(credentials);
	}
	
	/**
	 * Inspect the authorization header. By default support basic authentication / access_token.
	 * 1. OAuth2 authorization header
	 * 2. Access token from request
	 * 3. Access token from cookie
	 * 
	 * @param req
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected Principal requestCredentials(HttpServletRequest req) throws ServletException, IOException {
		String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
		
		//1. CHECK AUTHORIZATION HEADER SCHEME XXX (+1 to exclude space)
		if(authorization != null) {
			if(authorization.startsWith(IdParameters.Scheme.Bearer.name())) {
				return	new TokenCredentials(authorization.substring(IdParameters.Scheme.Bearer.name().length() + 1));
			} else if(authorization.startsWith(IdParameters.Scheme.Basic.name())) {
				return new ClientCredentials(authorization.substring(IdParameters.Scheme.Basic.name().length() + 1));
			}
			
			//NOT MEAN TO BE
			logger.debug("Unknown authorization header: {}", authorization);
		}
		
		//2. DOUBLE CHECK for access token (AUTHZ, PARAM, HEADER, COOKIE...)
		String token = req.getParameter(IdParameters.PARAM_ACCESS_TOKEN);
		if(token == null && this.allowsCookie) {
			Cookie cookie = RequestWrapper.getCookie(req, IdParameters.PARAM_ACCESS_TOKEN);
			if(cookie != null) {
				token = cookie.getValue();
			}
		}
		return	(token == null? null : new TokenCredentials(token));
	}
}
