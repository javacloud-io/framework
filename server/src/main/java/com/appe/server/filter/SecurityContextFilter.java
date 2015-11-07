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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.appe.registry.AppeRegistry;
import com.appe.security.Authorization;
import com.appe.security.AuthenticationException;
import com.appe.security.Authenticator;
import com.appe.security.internal.Authenticators;
import com.appe.security.internal.Credentials;
import com.appe.security.internal.ClientCredentials;
import com.appe.security.internal.IdPConstants;
import com.appe.security.internal.TokenCredentials;
import com.appe.server.internal.RequestWrapper;
import com.appe.util.Objects;

/**
 * Simply process the authentication if any provided => authenticate and inject context if SUCCESS.
 * We are looking at <allow-cookie> and an <authenticator> parameters to indicate how the authorization process taking place.
 * 
 * <init-param>
 *	<param-name>authenticator</param-name>
 *	<param-value>list of binding names</param-value>		
 * </init-param>
 * <init-param>
 *	<param-name>allowCookie</param-name>
 *	<param-value></param-value>		
 * </init-param>
 * 
 * @author ho
 *
 */
public class SecurityContextFilter extends ServletFilter {
	protected boolean 	allowCookie;		//allows access cookie
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
		this.allowCookie= Boolean.valueOf(getInitParameter("allow-cookie"));
		
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
		this.authenticator = new Authenticators(authenticators);
	}
	
	/**
	 * ONLY INJECT THE SECURITY CONTEXT IF GRANTED!
	 * 
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		try {
			Authorization authzGrant = doAuthenticate(req);
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
	protected Authorization doAuthenticate(HttpServletRequest req) throws ServletException, IOException, AuthenticationException {
		Credentials credentials = requestCredentials(req);
		
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
	protected Credentials requestCredentials(HttpServletRequest req) throws ServletException, IOException {
		String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
		
		//1. CHECK AUTHORIZATION HEADER SCHEME XXX (+1 to exclude space)
		if(!Objects.isEmpty(authorization)) {
			if(authorization.startsWith(IdPConstants.Scheme.Bearer.name())) {
				return	new TokenCredentials(authorization.substring(IdPConstants.Scheme.Bearer.name().length() + 1));
			} else if(authorization.startsWith(IdPConstants.Scheme.Basic.name())) {
				return new ClientCredentials(authorization.substring(IdPConstants.Scheme.Basic.name().length() + 1));
			} else {
				logger.debug("Unknown authorization header: {}", authorization);
			}
		}
		
		//2. DOUBLE CHECK for access token (AUTHZ, PARAM, HEADER, COOKIE...)
		String token = req.getParameter(IdPConstants.PARAM_ACCESS_TOKEN);
		if(token == null && this.allowCookie) {
			Cookie cookie = RequestWrapper.getCookie(req, IdPConstants.PARAM_ACCESS_TOKEN);
			if(cookie != null) {
				token = cookie.getValue();
			}
		}
		return	(token == null? null : new TokenCredentials(token));
	}
}
