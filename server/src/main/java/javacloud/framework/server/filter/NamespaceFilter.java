package javacloud.framework.server.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javacloud.framework.cdi.ServiceRegistry;
import javacloud.framework.security.NamespaceContext;
import javacloud.framework.server.internal.W3RequestWrapper;
import javacloud.framework.util.Objects;
/**
 * ALL APIs SHOULD PASSING THROUGH THE MAIN FILTER IF WOULD LIKE TO TRACK EVENT....
 * 
 * @author ho
 *
 */
public class NamespaceFilter extends ServletFilter {
	public static final String PARAM_NAMESPACE	= "_namespace_";
	public static final String HEADER_NAMESPACE	= "X-NAMESPACE";
	
	private NamespaceContext namespaceContext;
	public NamespaceFilter() {
	}
	
	/**
	 * 
	 */
	@Override
	protected void init() throws ServletException {
		this.namespaceContext = ServiceRegistry.get().getInstance(NamespaceContext.class);
	}
	
	/**
	 * Make sure to just look at query parameters!!!
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws ServletException, IOException {
		try {
			namespaceContext.set(requestNamespace(new W3RequestWrapper(req)));
			chain.doFilter(req, resp);
		} finally {
			namespaceContext.clear();
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
