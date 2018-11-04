package javacloud.framework.server.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import javacloud.framework.util.Codecs;
import javacloud.framework.util.Objects;
/**
 * Only parsing out request parameters from QUERY to avoid touching the BODY in case of:
 * application/x-www-form-urlencoded
 * 
 * @author ho
 *
 */
public class W3RequestWrapper extends HttpServletRequestWrapper {
	private Map<String, String[]> parameterMap;
	public W3RequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		String queryString = request.getQueryString();
		if(Objects.isEmpty(queryString)) {
			parameterMap = Objects.asMap();
		}  else {
			parameterMap = Objects.cast(Codecs.decodeURL(queryString, true));
		}
	}
	
	/**
	 * return from cache version
	 */
	@Override
	public String getParameter(String name) {
		String[] values = parameterMap.get(name);
		return (values == null || values.length == 0? null : values[0]);
	}
	
	/**
	 * TODO: 
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}
	
	/**
	 * TODO:
	 */
	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameterMap.keySet());
	}
	
	/**
	 * 
	 */
	@Override
	public String[] getParameterValues(String name) {
		return parameterMap.get(name);
	}
}
