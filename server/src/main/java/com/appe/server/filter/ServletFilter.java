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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Make sure everything is route to HTTP protocol.
 * 
 * @author tobi
 *
 */
public abstract class ServletFilter implements Filter {
	//SINGLE LOGGER FOR SERVLET
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected FilterConfig config;
	/**
	 * 
	 */
	@Override
	public final void init(FilterConfig config) throws ServletException {
		this.config = config;
		init();
	}
	
	/**
	 * Default configure the filter
	 * 
	 * @throws ServletException
	 */
	protected void init() throws ServletException {
	}
	
	/**
	 * 
	 * @return
	 */
	public FilterConfig getConfig() {
		return config;
	}
	
	/**
	 * 
	 * Delegate to http filter
	 */
	@Override
	public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
		throws IOException, ServletException {
		
		//CREATE & PREPARE CONTEXT
		doFilter((HttpServletRequest)req, (HttpServletResponse)resp, chain);
	}
	
	/**
	 * Simple filter chain
	 * 
	 * @param req
	 * @param resp
	 * @param chain
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException;
	
	
	/**
	 * 
	 */
	@Override
	public void destroy() {
	}
}
