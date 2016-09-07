package com.appe.samples.boilerplate.startup;

import javax.ws.rs.ApplicationPath;

import com.appe.framework.server.RestApplication;
/**
 * 
 * @author ho
 *
 */
@ApplicationPath("/v1/*")
public class MainApplication extends RestApplication {
	public MainApplication() {
		super();	//packages
	}
}
