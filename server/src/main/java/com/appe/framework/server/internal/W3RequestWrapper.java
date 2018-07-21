package com.appe.framework.server.internal;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.appe.framework.io.Dictionary;
import com.appe.framework.util.Dictionaries;
import com.appe.framework.util.Objects;
/**
 * Only parsing out request parameters from QUERY to avoid touching the BODY in case of:
 * application/x-www-form-urlencoded
 * 
 * @author ho
 *
 */
public class W3RequestWrapper extends HttpServletRequestWrapper {
	private Dictionary parameterMap;
	public W3RequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		String queryString = request.getQueryString();
		if(Objects.isEmpty(queryString)) {
			parameterMap = new Dictionary();
		}  else {
			parameterMap = Dictionaries.decodeURL(queryString);
		}
	}
	
	/**
	 * return from cache version
	 */
	@Override
	public String getParameter(String name) {
		return parameterMap.get(name);
	}
	
	/**
	 * TODO: 
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		return super.getParameterMap();
	}
	
	/**
	 * TODO:
	 */
	@Override
	public Enumeration<String> getParameterNames() {
		return super.getParameterNames();
	}
	
	/**
	 * 
	 */
	@Override
	public String[] getParameterValues(String name) {
		String value = getParameter(name);
		return value == null ? null : new String[] {value};
	}
}
