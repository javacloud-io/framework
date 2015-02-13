package com.appe.showcase.startup;

import com.appe.security.AuthenticationProvider;
import com.appe.showcase.security.DummyAuthenticationProvider;
import com.google.inject.AbstractModule;
/**
 * 
 * @author ho
 *
 */
public class ShowcaseModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AuthenticationProvider.class).to(DummyAuthenticationProvider.class);
	}
}
