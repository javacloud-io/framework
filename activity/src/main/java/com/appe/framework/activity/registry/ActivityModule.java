package com.appe.framework.activity.registry;

import java.util.List;

import javax.inject.Singleton;

import com.appe.framework.activity.EventManager;
import com.appe.framework.activity.impl.EventManagerImpl;
import com.appe.framework.internal.GuiceFactory;
import com.appe.framework.internal.GuiceModule;

import com.google.inject.Injector;
import com.google.inject.Provides;
/**
 * Register all the basic module
 * 
 * @author ho
 *
 */
public class ActivityModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(EventManager.class).to(EventManagerImpl.class);
	}
	
	/**
	 * SEARCH FOR ALL EVENT HANDLERs
	 * 
	 * @param injector
	 * @return
	 */
	@Provides
	@Singleton
	List<EventManager.Handler> getEventHandlers(Injector injector) {
		return GuiceFactory.getInstances(injector, EventManager.Handler.class);
	}
}
