/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.server.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.appe.AppeException;
import com.appe.registry.AppeLoader;
import com.appe.registry.AppeRegistry;
import com.appe.security.AccessDeniedException;
import com.appe.security.Authenticator;
import com.appe.security.Authentication;
import com.appe.security.AuthenticationException;
import com.appe.security.InvalidCredentialsException;
import com.appe.security.SimpleCredentials;
import com.appe.security.impl.ClientCredentials;
import com.appe.security.impl.IdPConstants;
import com.appe.security.impl.TokenCredentials;
import com.appe.util.Dictionaries;
import com.appe.util.Dictionary;
import com.appe.util.Objects;

/**
 * Simplest as possible security filter, we protect the WHOLE URL using FIX ROLES. Granularity will be enforce directly
 * at the resource level.
 * 
 * 1. Validate and inject authorization
 * 2. Send back the correct challenge
 * 
 * This filter will be a good replacement of built-in container security, very well integrated.
 * 
 * @author tobi
 */
public class AuthenticatorFilter extends ServletFilter {
	protected String   challengeScheme;
	protected String[] allowedRoles;
	protected String   loginPage;
	protected Authenticator authenticator;
	public AuthenticatorFilter() {
	}
	
	/**
	 * <init-param>
	 *	<param-name>login-page</param-name>
	 *	<param-value></param-value>		
	 * </init-param>
	 * 
	 * <init-param>
	 *	<param-name>challenge-scheme</param-name>
	 *	<param-value></param-value>		
	 * </init-param>
	 * 
	 * <init-param>
	 *	<param-name>allowed-roles</param-name>
	 *	<param-value></param-value>		
	 * </init-param>
	 * 
	 * <init-param>
	 *	<param-name>authenticator</param-name>
	 *	<param-value></param-value>		
	 * </init-param>
	 * 
	 * @param filterConfig
	 * @throws ServletException
	 */
	@Override
	protected void configure() throws ServletException {
		
		//AUTH SCHEME
		this.challengeScheme = config.getInitParameter("challenge-scheme");
		String roles = config.getInitParameter("allowed-roles");
		if(roles != null) {
			this.allowedRoles = Objects.toArray(roles, ",", true);
		}
		
		//LOGIN PAGE
		this.loginPage = config.getInitParameter("login-page");
		if(loginPage == null) {
			loginPage = IdPConstants.LOGIN_REDIRECT_URI;
		}
		
		//AUTHENTICATOR
		try {
			String authenticator = config.getInitParameter("authenticator");
			if(authenticator == null) {
				this.authenticator = AppeRegistry.get().getInstance(Authenticator.class);
			} else {
				Class<?> type = AppeLoader.getClassLoader().loadClass(authenticator);
				this.authenticator = (Authenticator)AppeRegistry.get().getInstance(type);
			}
		} catch(Exception ex) {
			throw new ServletException(ex);
		}
	}
	
	/**
	 * Make sure always authenticate any REQUEST coming through and chain the security context
	 * TODO: should support request already authenticated
	 */
	@Override
	public final void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		try {
			Authentication authzGrant = doAuthenticate(req);
			if(allowedRoles != null && !authzGrant.hasAnyRoles(allowedRoles)) {
				throw new AccessDeniedException();
			}
			
			chain.doFilter(RequestWrapper.wrap(req, authzGrant), resp);
		} catch(AuthenticationException ex) {
			responseError(req, resp, ex);
		}
	}
	
	/**
	 * Make sure to add enough context around the authentication...
	 * ALSO CHECK FOR CONTEXT & PERMISSION....
	 *  
	 * @param req
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	protected Authentication doAuthenticate(HttpServletRequest req) throws ServletException, IOException, AuthenticationException {
		SimpleCredentials credentials = requestCredentials(req);
		if(credentials == null) {
			logger.debug("Not found credentials, access denied!");
			throw new InvalidCredentialsException();
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
	protected SimpleCredentials requestCredentials(HttpServletRequest req) throws ServletException, IOException {
		String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
		
		//1. CHECK AUTHORIZATION HEADER SCHEME XXX (+1 to exclude space)
		if(!Objects.isEmpty(authorization)) {
			if(authorization.startsWith(IdPConstants.SCHEME_BEARER)) {
				return	new TokenCredentials(authorization.substring(IdPConstants.SCHEME_BEARER.length() + 1));
			} else if(authorization.startsWith(IdPConstants.SCHEME_BASIC)) {
				return new ClientCredentials(authorization.substring(IdPConstants.SCHEME_BASIC.length() + 1));
			} else {
				logger.debug("Unknown authorization header: {}", authorization);
			}
		}
		
		//2. DOUBLE CHECK for access token (AUTHZ, PARAM, HEADER, COOKIE...)
		String accessToken = req.getParameter(IdPConstants.PARAM_ACCESS_TOKEN);
		if(accessToken == null) {
			accessToken = requestCookie(req);
		}
		return	(accessToken == null? null : new TokenCredentials(accessToken));
	}
	
	/**
	 * Decode access token from cookie if any, due to the nature of unsecured environment, the cookie might need
	 * to decode and validate...
	 * 
	 * @param req
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected String requestCookie(HttpServletRequest req) throws ServletException, IOException {
		Cookie[] cookies = req.getCookies();
		if(cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (IdPConstants.PARAM_ACCESS_TOKEN.equals(cookie.getName())) {
					return	cookie.getValue();
				}
			}
		}
		return null;
	}
	
	/**
	 * Response challenge the authentication, for now just support REDIRECT or FORBIDDEN.
	 * 
	 * @param exception
	 * @param req
	 * @param resp
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void responseError(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception)
		throws ServletException, IOException {
		Dictionary entity = Objects.asDict(IdPConstants.PARAM_ERROR, exception.getReason(),
				IdPConstants.PARAM_STATE, req.getParameter(IdPConstants.PARAM_STATE));
				
		//ALWAYS ASSUMING REDIRECT
		if(challengeScheme == null || challengeScheme.isEmpty()) {
			String redirectUri = loginPage + (loginPage.indexOf('#') > 0? "&" : "#") +  Dictionaries.encodeURL(entity);
			resp.sendRedirect(RequestWrapper.buildURI(req, redirectUri));
		} else {
			//ANYTHING WILL ASSUMING AUTHZ ISSUE?
			resp.setHeader(HttpHeaders.WWW_AUTHENTICATE, challengeScheme);
			resp.setStatus(AppeException.isCausedBy(exception, AccessDeniedException.class) ?
					HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().write((Dictionaries.encodeURL(entity)));
		}
	}
}
