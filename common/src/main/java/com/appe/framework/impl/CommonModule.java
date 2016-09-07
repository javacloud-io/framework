package com.appe.framework.impl;

import com.appe.framework.AppeLocale;
import com.appe.framework.AppeNamespace;
import com.google.inject.AbstractModule;
/**
 * Make sure to be able to configure and inject the configuration properly.
 * 1. Be able to lookup AppeConfig directly
 * 2. Be able to inject any config annotate using AppeConfig.Bundle
 * 
 * @author ho
 *
 */
public class CommonModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AppeNamespace.class).to(AppeNamespaceImpl.class);
		bind(AppeLocale.class).to(AppeLocaleImpl.class);
	}
}
