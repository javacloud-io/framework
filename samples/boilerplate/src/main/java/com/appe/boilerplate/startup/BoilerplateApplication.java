package com.appe.boilerplate.startup;

import javax.ws.rs.ApplicationPath;

import com.appe.server.RestApplication;
/**
 * 
 * @author ho
 *
 */
@ApplicationPath("/v1/*")
public class BoilerplateApplication extends RestApplication {
	public BoilerplateApplication() {
		super();	//packages
	}
}
