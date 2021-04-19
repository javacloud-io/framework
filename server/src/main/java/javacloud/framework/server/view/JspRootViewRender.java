package javacloud.framework.server.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javacloud.framework.util.Objects;
/**
 * Root view is special one, it supports push state redirect in modern JavaScript framework.
 * 
 * 1. If a request servlet/path => always redirect to servlet with #hash encoded
 * 2. Render the view with correct VALUE.
 * 
 * Example: /login/resetpwd => /login#resetpwd for login servlet mapping
 * 			/login/resetpwd?userId=1234 => /login?userId=1234#resetpwd
 * 
 * @author ho
 *
 */
public class JspRootViewRender extends JspViewRender {
	private static final long serialVersionUID = -4041202495233287415L;

	/**
	 * Forward to the correct view if specified
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getPathInfo();
		if (!Objects.isEmpty(path)) {
			StringBuilder uriBuilder = new StringBuilder(req.getServletPath());
			String query = req.getQueryString();
			if (!Objects.isEmpty(query)) {
				uriBuilder.append("?").append(query);
			}
			
			//CHOP THE forward slash OFF
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			
			//FLIP EVERY TO HASH
			if (!Objects.isEmpty(path)) {
				uriBuilder.append("#").append(path);
			}
			resp.sendRedirect(uriBuilder.toString());
		} else {
			super.doGet(req, resp);
		}
	}
}
