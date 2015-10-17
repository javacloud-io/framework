package com.appe.server.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appe.util.Objects;
/**
 * Map a given servlet to a JSP page, by default the page will be located under WEB-INF
 * 
 * @author ho
 *
 */
public class JspViewRender extends HttpServlet {
	private static final long serialVersionUID = 1336457408255780274L;
	
	/**
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher(jspView(req)).forward(req, resp);
	}
	
	/**
	 * return jsp page to render
	 * 
	 * @param req
	 * @return
	 * @throws ServletException
	 */
	protected String jspView(HttpServletRequest req) throws ServletException {
		String path = req.getPathInfo();
		String requestURI = Objects.isEmpty(path)? req.getServletPath() : (req.getServletPath() + path);
		
		//JSP VIEW
		return	"/WEB-INF" + requestURI + ".jsp";
	}
}
