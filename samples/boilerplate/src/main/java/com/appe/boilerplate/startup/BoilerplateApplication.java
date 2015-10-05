package com.appe.boilerplate.startup;

import javax.ws.rs.ApplicationPath;

import com.appe.server.startup.DefaultApplication;
/**
 * 
 * @author ho
 *
 */
@ApplicationPath("/v1/*")
public class BoilerplateApplication extends DefaultApplication {
	public BoilerplateApplication() {
		super();	//packages
	}
}
