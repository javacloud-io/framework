package com.appe.framework.impl;

import java.util.List;

import com.appe.framework.activity.EventManager;
import com.appe.framework.activity.impl.EventManagerImpl;
import com.appe.framework.internal.GuiceFactory;
import com.appe.framework.internal.GuiceModule;
import com.appe.framework.resource.ResourceBundleManager;
import com.appe.framework.resource.impl.ResourceBundleManagerImpl;
import com.google.inject.Injector;
import com.google.inject.Provides;
/**
 * Register all the basic module
 * 
 * @author ho
 *
 */
public class ServiceModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(ResourceBundleManager.class).to(ResourceBundleManagerImpl.class);
		bind(EventManager.class).to(EventManagerImpl.class);
	}
	
	/**
	 * SEARCH FOR ALL EVENT HANDLERs
	 * 
	 * @param injector
	 * @return
	 */
	@Provides
	List<EventManager.Handler> getEventHandlers(Injector injector) {
		return GuiceFactory.getInstances(injector, EventManager.Handler.class);
	}
}
