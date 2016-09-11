package com.appe.samples.showcase.startup;

import javax.ws.rs.ApplicationPath;

import com.appe.framework.server.RestApplication;

/**
 * Route all the API packages to the same v1 resource root.
 * 
 * @author ho
 *
 */
@ApplicationPath("/v1/*")
public class MainApplication extends RestApplication {
	/**
	 * Configure how the resource should be combine, object should be inject...
	 */
	public MainApplication() {
		super("com.appe.samples.showcase.restapi");
	}
}
