package io.javacloud.framework.server.internal;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import io.javacloud.framework.security.AccessGrant;
import io.javacloud.framework.security.internal.Permissions;

/**
 * WRAPP THE GRANT AUTHENTICATION CONTEXT to provide an extra layer of protection on regular servlet authorization.
 * In theory we shouldn't do that, since the authorization challenge just able to send back 403 always. Of course it can be
 * customize using a 403.xxx error page it's never good enough!!
 * 
 * @author tobi
 *
 */
public class RequestWrapper extends HttpServletRequestWrapper {
	private AccessGrant authz;
	RequestWrapper(HttpServletRequest request, AccessGrant authz) {
		super(request);
		this.authz = authz;
	}
	
	/**
	 * return authenticated user name
	 */
	@Override
	public String getRemoteUser() {
		return (authz != null? authz.getName() : null);
	}
	
	/**
	 * return true if user has any role name
	 */
	@Override
	public boolean isUserInRole(String roleName) {
		return(authz != null? Permissions.hasAny(authz, roleName) : false);
	}
	
	/**
	 * return the user principal
	 */
	@Override
	public Principal getUserPrincipal() {
		return authz;
	}
	
	/**
	 * Wrap the authorization around the request itself.
	 * @param request
	 * @param authz
	 * @return
	 */
	public static final HttpServletRequest wrap(HttpServletRequest request, AccessGrant authz) {
		if(request instanceof RequestWrapper) {
			((RequestWrapper)request).authz = authz;
		} else {
			request = new RequestWrapper(request, authz);
		}
		return request;
	}
	
	/**
	 * Decode access token from cookie if any, due to the nature of unsecured environment, the cookie might need
	 * to decode and validate...
	 * 
	 * @param req
	 * @param cookieName
	 * 
	 * @return
	 */
	public static final Cookie getCookie(HttpServletRequest req, String cookieName) {
		Cookie[] cookies = req.getCookies();
		if(cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					return	cookie;
				}
			}
		}
		return null;
	}
	
	/**
	 * return URI to the correct scheme/port
	 * 
	 * @param scheme
	 * @param port
	 * @param server
	 * @return
	 */
	public static final StringBuilder buildURI(String scheme, int port, String server) {
		// TRYING TO EXCLUDE PORT NUMBER IF STANDARD HTTP/HTTPS
		StringBuilder uri = new StringBuilder(scheme)
			.append("://")
			.append(server);
		if ((port <= 0)
			|| (port == 80 && scheme.equals("http"))
			|| (port == 443 && scheme.equals("https"))) {
			return uri;
		}
		
		// ALWAYS INCLUDE PORT
		return uri.append(":").append(port);
	}
	
	/**
	 * return base server URI and path to the server itself. ASSUMING NO TRAILING SLASH
	 * 
	 * @param req
	 * @return
	 */
	public static final StringBuilder buildURI(HttpServletRequest req) {
		return	buildURI(req.getScheme(), req.getServerPort(), req.getServerName())
				.append(req.getContextPath());
	}
	
	/**
	 * 
	 * @param req
	 * @param location
	 * @return
	 */
	public static final String buildURI(HttpServletRequest req, String location) {
		if(location.startsWith("https://") || location.startsWith("http://")) {
			return location;
		}
		
		StringBuilder uri = buildURI(req.getScheme(), req.getServerPort(), req.getServerName())
				.append(req.getContextPath());
		if(!location.startsWith("/")) {
			uri.append("/");
		}
		return	uri.append(location).toString();
	}
}