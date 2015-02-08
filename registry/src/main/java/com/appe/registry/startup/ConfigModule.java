package com.appe.registry.startup;

import com.appe.registry.AppeConfig;
import com.appe.registry.impl.AppeConfigImpl;
import com.google.inject.AbstractModule;
/**
 * Make sure to be able to configure and inject the configuration properly.
 * 1. Be able to lookup AppeConfig directly
 * 2. Be able to inject any config annotate using AppeConfig.Bundle
 * 
 * @author ho
 *
 */
public class ConfigModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AppeConfig.class).to(AppeConfigImpl.class);
	}
}
