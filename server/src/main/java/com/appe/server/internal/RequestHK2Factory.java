package com.appe.server.internal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.glassfish.hk2.api.Factory;

import com.appe.security.Authorization;
/**
 * For easy integrate with jersey resource
 * 
 * @author ho
 *
 */
public class RequestHK2Factory implements Factory<Authorization> {
	private HttpServletRequest request;
	
	@Inject
    public RequestHK2Factory(HttpServletRequest request) {
        this.request = request;
    }
	
	/**
	 * return authorization from attribute if any found.
	 */
	@Override
	public Authorization provide() {
		return	(Authorization)request.getAttribute(Authorization.class.getName());
	}
	
	/**
	 * 
	 */
	@Override
	public void dispose(Authorization authz) {
	}
}
