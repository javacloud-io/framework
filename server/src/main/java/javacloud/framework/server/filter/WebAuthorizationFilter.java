package javacloud.framework.server.filter;

import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AccessGrant;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.IdParameters;
import javacloud.framework.security.InvalidCredentialsException;
import javacloud.framework.security.internal.Permissions;
import javacloud.framework.server.internal.RequestWrapper;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Converters;
import javacloud.framework.util.InternalException;
import javacloud.framework.util.Objects;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;

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
		if (loginPage == null) {
			loginPage = IdParameters.LOGIN_REDIRECT_URI;
		}
		
		//ROLES
		String roles = getInitParameter("permissions");
		if (roles != null) {
			this.permissions = Converters.toArray(roles, ",", true);
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
			AccessGrant authzGrant = doAuthenticate(req);
			if (authzGrant == null) {
				throw new InvalidCredentialsException();
			}
			
			//NOT GRANTED
			if(permissions != null && !Permissions.hasAny(authzGrant, permissions)) {
				throw new AccessDeniedException();
			}
			
			chain.doFilter(RequestWrapper.wrap(req, authzGrant), resp);
		} catch (AuthenticationException ex) {
			responseError(req, resp, ex);
		} catch (ServletException ex) {
			AuthenticationException aex = InternalException.getCause(ex, AuthenticationException.class);
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
		Map<String, Object> entity = Objects.asMap(IdParameters.PARAM_ERROR, exception.getReason(),
				IdParameters.PARAM_STATE, req.getParameter(IdParameters.PARAM_STATE));
				
		//ALWAYS ASSUMING REDIRECT
		if (Objects.isEmpty(challengeScheme)) {
			String redirectUri = loginPage + (loginPage.indexOf('#') > 0? "&" : "#") +  Codecs.UrlEncoder.apply(entity, true);
			resp.sendRedirect(RequestWrapper.buildURI(req, redirectUri));
		} else {
			//ANYTHING WILL ASSUMING AUTHZ ISSUE?
			resp.setHeader(HttpHeaders.WWW_AUTHENTICATE, challengeScheme);
			resp.setStatus(InternalException.isCausedBy(exception, AccessDeniedException.class) ?
					HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().write((Codecs.UrlEncoder.apply(entity, true)));
		}
	}
}
