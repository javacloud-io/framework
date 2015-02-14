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