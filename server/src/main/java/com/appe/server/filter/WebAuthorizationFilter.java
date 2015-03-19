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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.appe.security.impl.BasicCredentials;
import com.appe.security.impl.IdPConstants;
import com.appe.security.impl.TokenCredentials;

/**
 * Support extract access cookie from session, the cookie using the same name.
 * 
 * @author ho
 *
 */
public class WebAuthorizationFilter extends AuthorizationFilter {
	protected String accessCookie;
	public WebAuthorizationFilter() {
	}
	
	/**
	 * Configurable the session cookie
	 * 
	 */
	@Override
	protected void configure() throws ServletException {
		super.configure();
		this.accessCookie = config.getInitParameter("access-cookie");
		if(this.accessCookie == null) {
			this.accessCookie = IdPConstants.PARAM_ACCESS_TOKEN;
		}
	}

	/**
	 * Look into to session cookie to see if any available.
	 */
	@Override
	protected BasicCredentials requestCredentials(HttpServletRequest req) throws ServletException, IOException {
		BasicCredentials credentials = super.requestCredentials(req);
		if(credentials == null) {
			String accessToken = requestCookie(req);
			if(accessToken != null) {
				credentials = new TokenCredentials(accessToken);
			}
		}
		return	credentials;
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
				if (accessCookie.equals(cookie.getName())) {
					return	cookie.getValue();
				}
			}
		}
		return null;
	}
}
