package com.appe.server.filter;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.appe.security.Authorization;

/**
 * WRAPP THE GRANT AUTHENTICATION CONTEXT to provide an extra layer of protection on regular servlet authorization.
 * In theory we shouldn't do that, since the authorization challenge just able to send back 403 always. Of course it can be
 * customize using a 403.xxx error page it's never good enough!!
 * 
 * @author tobi
 *
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {
	private Authorization authorization;
	HttpRequestWrapper(HttpServletRequest request, Authorization authorization) {
		super(request);
		
		//MAKE SURE TO KEEP AS BOTH PLACE!
		this.authorization = authorization;
	}
	
	/**
	 * return authenticated user name
	 */
	@Override
	public String getRemoteUser() {
		return (authorization != null? authorization.getName() : null);
	}
	
	/**
	 * return true if user has any role name
	 */
	@Override
	public boolean isUserInRole(String roleName) {
		return(authorization != null? authorization.hasAnyPermissions(roleName) : false);
	}
	
	/**
	 * return the user principal
	 */
	@Override
	public Principal getUserPrincipal() {
		return authorization;
	}
	
	/**
	 * Wrap the authorization around the request itself.
	 * @param request
	 * @param authorization
	 * @return
	 */
	public static final HttpServletRequest wrap(HttpServletRequest request, Authorization authorization) {
		if(request instanceof HttpRequestWrapper) {
			((HttpRequestWrapper)request).authorization = authorization;
		} else {
			request = new HttpRequestWrapper(request, authorization);
		}
		return request;
	}
}