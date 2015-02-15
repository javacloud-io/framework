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

import com.appe.AppeNamespace;
import com.appe.registry.AppeRegistry;
import com.appe.util.Objects;
/**
 * ALL APIs SHOULD PASSING THROUGH THE MAIN FILTER IF WOULD LIKE TO TRACK EVENT....
 * 
 * @author ho
 *
 */
public class NamespaceFilter extends HttpServletFilter {
	public static final String PARAM_NAMESPACE	= "__namespace";
	public static final String HEADER_NAMESPACE	= "X-Namespace";
	
	private AppeNamespace appeNamespace;
	public NamespaceFilter() {
	}
	
	/**
	 * 
	 */
	@Override
	protected void configure() throws ServletException {
		this.appeNamespace = AppeRegistry.get().getInstance(AppeNamespace.class);
	}
	
	/**
	 * 
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws ServletException, IOException {
		try {
			appeNamespace.set(requestNamespace(req));
			chain.doFilter(req, resp);
		} finally {
			appeNamespace.clear();
		}
		
	}

	/**
	 * IF ANY NAMESPACE => MAKE SURE TO PREPARE THEM... for testing by default we just lookup from QUERY PARAMETER or HEADER
	 * BY DEFAULT APPEspace will be figured out automatically using CLIENT KEY however if a space allows application to run
	 * it also can be access as well.
	 * 
	 * @param req
	 * @return
	 * @throws ServletException
	 */
	protected String requestNamespace(HttpServletRequest req) throws ServletException {
		String namespace = req.getParameter(PARAM_NAMESPACE);
		if(Objects.isEmpty(namespace)) {
			namespace = req.getHeader(HEADER_NAMESPACE);
		}
		return namespace;
	}
}
