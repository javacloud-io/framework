package com.appe.framework.activity.impl;

import java.util.List;

import javax.inject.Singleton;

import com.appe.framework.activity.EventManager;
import com.appe.framework.internal.GuiceFactory;
import com.appe.framework.internal.GuiceModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
/**
 * Default module will scan for all the EventHandler and pass to event manager
 * 
 * @author ho
 *
 */
public class ServiceModule extends GuiceModule {
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
