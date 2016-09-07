package com.appe.framework.server.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.appe.framework.AppeException;
import com.appe.framework.security.AccessDeniedException;
import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.Authorization;
import com.appe.framework.security.IdParameters;
import com.appe.framework.security.InvalidCredentialsException;
import com.appe.framework.server.internal.RequestWrapper;
import com.appe.framework.util.Dictionaries;
import com.appe.framework.util.Dictionary;
import com.appe.framework.util.Objects;

/**
 * Simplest as possible security filter, we protect the WHOLE URL using FIX ROLES. Granularity will be enforce directly
 * at the resource level.
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
 *	<param-name>permissions</param-name>
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
public class WebAuthorizationFilter extends SecurityContextFilter {
	protected String   challengeScheme;		//redirect, basic, oauth...
	protected String   loginPage;			//where is the login page if redirect
	protected String[] permissions;			//any which roles is granted is OK
	
	public WebAuthorizationFilter() {
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
			loginPage = IdParameters.LOGIN_REDIRECT_URI;
		}
		
		//ROLES
		String roles = getInitParameter("permissions");
		if(roles != null) {
			this.permissions = Objects.toArray(roles, ",", true);
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
			if(permissions != null && !authzGrant.hasAnyRoles(permissions)) {
				throw new AccessDeniedException();
			}
			
			chain.doFilter(RequestWrapper.wrap(req, authzGrant), resp);
		} catch(AuthenticationException ex) {
			responseError(req, resp, ex);
		} catch(ServletException ex) {
			AuthenticationException aex = AppeException.findCause(ex, AuthenticationException.class);
			if(aex == null) {
				throw ex;
			}
			responseError(req, resp, aex);
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
		Dictionary entity = Objects.asDict(IdParameters.PARAM_ERROR, exception.getReason(),
				IdParameters.PARAM_STATE, req.getParameter(IdParameters.PARAM_STATE));
				
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
