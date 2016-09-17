package com.appe.framework.resource.registry;

import com.appe.framework.internal.GuiceModule;
import com.appe.framework.resource.ResourceManager;
import com.appe.framework.resource.impl.ResourceManagerImpl;
/**
 * 
 * @author ho
 *
 */
public class DefaultModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(ResourceManager.class).to(ResourceManagerImpl.class);
	}
}
