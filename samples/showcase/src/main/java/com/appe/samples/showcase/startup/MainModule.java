package com.appe.samples.showcase.startup;

import com.appe.framework.internal.GuiceModule;
import com.appe.framework.security.Authenticator;
/**
 * 
 * @author ho
 *
 */
public class MainModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(Authenticator.class).to(DummyAuthenticator.class);
	}
}
