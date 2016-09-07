package com.appe.framework.server.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appe.framework.AppeRegistry;
import com.appe.framework.AppeNamespace;
import com.appe.framework.util.Objects;
/**
 * ALL APIs SHOULD PASSING THROUGH THE MAIN FILTER IF WOULD LIKE TO TRACK EVENT....
 * 
 * @author ho
 *
 */
public class NamespaceFilter extends ServletFilter {
	public static final String PARAM_NAMESPACE	= "_namespace_";
	public static final String HEADER_NAMESPACE	= "X-Namespace";
	
	private AppeNamespace appeNamespace;
	public NamespaceFilter() {
	}
	
	/**
	 * 
	 */
	@Override
	protected void init() throws ServletException {
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
