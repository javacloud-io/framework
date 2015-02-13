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
import com.appe.security.AuthenticationProvider;
import com.appe.security.Authorization;
import com.appe.security.AuthorizationException;
import com.appe.security.InvalidCredentialsException;
import com.appe.security.SimplePrincipal;
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
public class HttpSecurityFilter extends HttpServletFilter {
	public static final String PARAM_ACCESS_TOKEN	= "access_token";
	public static final String PARAM_ERROR 			= "error";
	
	public static final String SCHEME_BASIC 		= "Basic";	//Client Basic
	public static final String SCHEME_BEARER		= "Bearer";	//Oauth2 Bearer
	
	protected String   loginPage;
	protected String   authScheme;
	protected String[] allowRoles;
	protected AuthenticationProvider authenticator;
	public HttpSecurityFilter() {
	}
	
	/**
	 * <init-param>
	 *	<param-name>login-page</param-name>
	 *	<param-value></param-value>		
	 * </init-param>
	 * 
	 * <init-param>
	 *	<param-name>auth-scheme</param-name>
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
	 * @param filterConfig
	 * @throws ServletException
	 */
	@Override
	protected void configure() throws ServletException {
		//BASIC PARAMETERS
		this.loginPage = filterConfig.getInitParameter("login-page");
		if(loginPage == null) {
			loginPage = filterConfig.getServletContext().getRealPath("/login");
		}
		this.authScheme = filterConfig.getInitParameter("auth-scheme");
		String roles = filterConfig.getInitParameter("allow-roles");
		if(roles != null) {
			this.allowRoles = roles.split("\\s*,\\s*");
		}
		
		//RESOLVE AUTHENTICATOR
		try {
			String authenticator = filterConfig.getInitParameter("authenticator");
			if(authenticator == null) {
				this.authenticator = AppeRegistry.get().getInstance(AuthenticationProvider.class);
			} else {
				Class<?> zclass = AppeLoader.getClassLoader().loadClass(authenticator);
				this.authenticator = (AuthenticationProvider)AppeRegistry.get().getInstance(zclass);
			}
		} catch(Exception ex) {
			throw new ServletException(ex);
		}
	}
	
	/**
	 * Make sure always authenticate any REQUEST coming through and chain the security context
	 * 
	 */
	@Override
	public final void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {
		try {
			Authorization authzGrant = doAuthenticate(req);
			if(allowRoles != null && !authzGrant.hasAnyPermissions(allowRoles)) {
				throw new AccessDeniedException();
			}
			
			chain.doFilter(HttpRequestWrapper.wrap(req, authzGrant), resp);
		} catch(Throwable ex) {
			responseError(ex, resp);
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
	 * @throws AuthorizationException
	 */
	protected Authorization doAuthenticate(HttpServletRequest req) throws ServletException, IOException, AuthorizationException {
		SimplePrincipal credentials = requestCredentials(req);
		if(credentials == null) {
			logger.debug("Not found credentials, access denied!");
			throw new InvalidCredentialsException();
		}
		
		//TODO: OK, FILL IN SOME REQUEST DETALS
		return	authenticator.authenticate(credentials);
	}
	
	/**
	 * Inspect the authorization header. By default support basic authentication / access_token.
	 * 1. OAuth2 authorization header
	 * 2. 
	 * 
	 * @param req
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected SimplePrincipal requestCredentials(HttpServletRequest req) throws ServletException, IOException {
		String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
		
		//1. CHECK AUTHORIZATION HEADER SCHEME XXX (+1 to exclude space)
		if(!Objects.isEmpty(authorization)) {
			if(authorization.startsWith(SCHEME_BEARER)) {
				return	new SimplePrincipal(authorization.substring(SCHEME_BEARER.length() + 1));
			} else if(authorization.startsWith(SCHEME_BASIC)) {
				return new SimplePrincipal(authorization.substring(SCHEME_BASIC.length() + 1));
			} else {
				logger.debug("Unknown authorization header: {}", authorization);
			}
		}
		
		//2. DOUBLE CHECK for access token (AUTHZ, PARAM, HEADER, COOKIE...)
		String accessToken = req.getParameter(PARAM_ACCESS_TOKEN);
		if(accessToken == null) {
			Cookie[] cookies = req.getCookies();
			if(cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					if (PARAM_ACCESS_TOKEN.equals(cookie.getName())) {
						accessToken = cookie.getValue();
						break;
					}
				}
			}
		}
		return	(accessToken == null? null : new SimplePrincipal(accessToken));
	}
	
	/**
	 * Response challenge the authentication, for now just support REDIRECT or FORBIDDEN.
	 * 
	 * @param exception
	 * @param resp
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void responseError(Throwable exception, HttpServletResponse resp) throws ServletException, IOException {
		//BASIC ERROR MESSAGE FOR NOW
		Dictionary entity = Objects.asDict(PARAM_ERROR, exception.getMessage());
		
		//ALWAYS ASSUMING BASIC AUTH
		if(authScheme == null || authScheme.isEmpty()) {
			String redirectUri = loginPage + (loginPage.indexOf('#') > 0? "&" : "#") +  Dictionaries.encodeURL(entity);
			resp.sendRedirect(redirectUri);
			//responseRedirect(loginUri + (loginUri.indexOf('?') > 0? "&" : "?") +  Dictionaries.encodeURL(entity));
		} else {
			//ANYTHING WILL ASSUMING AUTHZ ISSUE?
			resp.setHeader(HttpHeaders.WWW_AUTHENTICATE, authScheme);
			resp.setStatus(AppeException.isCausedBy(exception, AccessDeniedException.class) ?
					HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().print(Dictionaries.encodeURL(entity));
		}
	}
}
