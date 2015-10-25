package com.appe.ext.impl;

import java.util.List;

import javax.ws.rs.client.Client;

import com.appe.ext.AppeNamespace;
import com.appe.ext.EventManager;
import com.appe.ext.internal.HttpClientProvider;
import com.appe.ext.internal.JacksonMapper;
import com.appe.registry.internal.GuiceFactory;
import com.appe.registry.internal.GuiceModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import com.google.inject.Provides;
/**
 * Register all the basic module
 * 
 * @author ho
 *
 */
public class CommonModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(AppeNamespace.class).to(ThreadLocalNamespace.class);
		bind(ObjectMapper.class).to(JacksonMapper.class);
		bind(EventManager.class).to(EventManagerImpl.class);
		bind(Client.class).toProvider(HttpClientProvider.class);
	}
	
	//SEARCH FOR ALL EVENT HANDLERs
	@Provides
	List<EventManager.Handler> getEventHandlers(Injector injector) {
		return GuiceFactory.getInstances(injector, EventManager.Handler.class);
	}
}
