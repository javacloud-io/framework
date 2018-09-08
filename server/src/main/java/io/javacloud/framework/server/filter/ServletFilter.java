package io.javacloud.framework.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Make sure everything is route to HTTP protocol.
 * 
 * @author tobi
 *
 */
public abstract class ServletFilter implements Filter {
	private FilterConfig config;
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
	 * return initial parameter value if any
	 * 
	 * @param name
	 * @return
	 */
	protected String getInitParameter(String name) {
		return config.getInitParameter(name);
	}
	
	/**
	 * return initial parameter with default value if not provided
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	protected String getInitParameter(String name, String defaultValue) {
		String value = config.getInitParameter(name);
		return (value == null? defaultValue : value);
	}
	
	/**
	 * 
	 * @return
	 */
	protected FilterConfig getConfig() {
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
