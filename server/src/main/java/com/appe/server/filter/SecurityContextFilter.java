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

import com.appe.registry.AppeLoader;
import com.appe.registry.AppeRegistry;
import com.appe.security.Authentication;
import com.appe.security.AuthenticationException;
import com.appe.security.Authenticator;
import com.appe.security.IdPConstants;
import com.appe.security.internal.BasicCredentials;
import com.appe.security.internal.ClientCredentials;
import com.appe.security.internal.TokenCredentials;
import com.appe.util.Objects;

/**
 * Simply process the authentication if any provided => authenticate and inject context if SUCCESS.
 * 
 * @author ho
 *
 */
public class SecurityContextFilter extends ServletFilter {
	protected String	accessToken;		//access token parameter
	protected String 	accessCookie;		//access cookie name
	
	protected Authenticator authenticator;
	public SecurityContextFilter() {
	}
	
	/**
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
	protected void init() throws ServletException {
		//OPTIONALS TOKEN PARAM
		this.accessToken = getInitParameter("access-token");
		this.accessCookie= getInitParameter("access-cookie");
		
		//AUTHENTICATOR
		try {
			String authenticator = getInitParameter("authenticator");
			if(authenticator == null) {
				this.authenticator = AppeRegistry.get().getInstance(Authenticator.class);
			} else {
				Class<?> type = AppeLoader.getClassLoader().loadClass(authenticator);
				this.authenticator = (Authenticator)AppeRegistry.get().getInstance(type);
			}
		} catch(ClassNotFoundException ex) {
			throw new ServletException(ex);
		}
	}
	
	/**
	 * ONLY INJECT THE SECURITY CONTEXT IF GRANTED!
	 * 
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		try {
			Authentication authzGrant = doAuthenticate(req);
			if(authzGrant != null) {
				chain.doFilter(RequestWrapper.wrap(req, authzGrant), resp);
			} else {
				chain.doFilter(req, resp);
			}
		} catch(AuthenticationException ex) {
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
	protected Authentication doAuthenticate(HttpServletRequest req) throws ServletException, IOException, AuthenticationException {
		BasicCredentials credentials = extractCredentials(req);
		
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
	protected BasicCredentials extractCredentials(HttpServletRequest req) throws ServletException, IOException {
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
		String token = null;
		if(this.accessToken != null) {
			token = req.getParameter(this.accessToken);
		}
		if(token == null && this.accessCookie != null) {
			Cookie cookie = RequestWrapper.getCookie(req, this.accessCookie);
			if(cookie != null) {
				token = cookie.getValue();
			}
		}
		return	(token == null? null : new TokenCredentials(token));
	}
}
