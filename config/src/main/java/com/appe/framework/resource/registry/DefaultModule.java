package com.appe.framework.resource.registry;

import com.appe.framework.internal.GuiceModule;
import com.appe.framework.resource.ResourceBundleManager;
import com.appe.framework.resource.impl.ResourceBundleManagerImpl;
/**
 * 
 * @author ho
 *
 */
public class DefaultModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(ResourceBundleManager.class).to(ResourceBundleManagerImpl.class);
	}
}
