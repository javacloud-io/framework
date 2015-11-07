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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.appe.AppeException;
import com.appe.security.AccessDeniedException;
import com.appe.security.Authorization;
import com.appe.security.AuthenticationException;
import com.appe.security.InvalidCredentialsException;
import com.appe.security.internal.IdPConstants;
import com.appe.server.internal.RequestWrapper;
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
 * <init-param>
 *	<param-name>challenge-scheme</param-name>
 *	<param-value></param-value>		
 * </init-param>
 * 
 * <init-param>
 *	<param-name>login-page</param-name>
 *	<param-value></param-value>		
 * </init-param>
 * 
 * <init-param>
 *	<param-name>allow-roles</param-name>
 *	<param-value></param-value>		
 * </init-param>
 * 
 * <init-param>
 *	<param-name>authenticator</param-name>
 *	<param-value></param-value>		
 * </init-param>
 * 
 * @author tobi
 */
public class AuthorizationFilter extends SecurityContextFilter {
	protected String   challengeScheme;		//redirect, basic, oauth...
	protected String   loginPage;			//where is the login page if redirect
	protected String[] allowRoles;			//which roles is GOOD
	
	public AuthorizationFilter() {
	}
	
	/**
	 * 
	 * @param filterConfig
	 * @throws ServletException
	 */
	@Override
	protected void init() throws ServletException {
		super.init();
		
		//AUTH SCHEME
		this.challengeScheme = getInitParameter("challenge-scheme");
		
		//LOGIN PAGE
		this.loginPage = getInitParameter("login-page");
		if(loginPage == null) {
			loginPage = IdPConstants.LOGIN_REDIRECT_URI;
		}
		
		//ROLES
		String roles = getInitParameter("allow-roles");
		if(roles != null) {
			this.allowRoles = Objects.toArray(roles, ",", true);
		}
	}
	
	/**
	 * Make sure always authenticate any REQUEST coming through and chain the security context
	 * NULL GRANT ~ INVALID CREDS
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		try {
			Authorization authzGrant = doAuthenticate(req);
			if(authzGrant == null) {
				throw new InvalidCredentialsException();
			}
			
			//NOT GRANTED
			if(allowRoles != null && !authzGrant.hasAnyRoles(allowRoles)) {
				throw new AccessDeniedException();
			}
			
			chain.doFilter(RequestWrapper.wrap(req, authzGrant), resp);
		} catch(AuthenticationException ex) {
			responseError(req, resp, ex);
		}
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
