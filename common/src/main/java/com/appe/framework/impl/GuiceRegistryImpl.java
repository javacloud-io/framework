package com.appe.framework.impl;

import java.util.List;

import com.appe.framework.AppeRegistry;
import com.appe.framework.internal.AnnotatedName;
import com.appe.framework.internal.GuiceBuilder;
import com.appe.framework.internal.GuiceFactory;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;

/**
 * Basic implementation using google juice and service override at runtime level. By default it will load a file:
 * META-INF/registry-modules.guice => then will perform overriding with XXX.1. It then can be perform a special override
 * using only a current class loading XXX.2.
 * 
 * @author ho
 *
 */
public class GuiceRegistryImpl extends AppeRegistry {
	private final Injector injector;
	/**
	 * 
	 */
	public GuiceRegistryImpl() {
		injector = createInjector();
	}
	
	/**
	 * return an instance of any service interface
	 * 
	 */
	@Override
	public final <T> T getInstance(Class<T> type) {
		return injector.getInstance(type);
	}
	
	/**
	 * The name always need to bind using Guice @Named
	 */
	@Override
	public <T> T getInstance(Class<T> type, String name) {
		return injector.getInstance(Key.get(type, new AnnotatedName(name)));
	}
	
	/**
	 * Find all instances of the service type.
	 */
	@Override
	public <T> List<T> getInstances(Class<T> type) {
		return GuiceFactory.getInstances(injector, type);
	}

	/**
	 * For internal use only, can be call using reflection. Might be use for integration purpose..
	 * 
	 * @return
	 */
	public final Injector getInjector() {
		return injector;
	}
	
	/**
	 * By default we always looking for service with default profile unless it's override at runtime.
	 * 
	 * @return
	 */
	protected Injector createInjector() {
		return GuiceFactory.createInjector(new GuiceBuilder.StageBuilder(Stage.PRODUCTION), "META-INF/registry-services.guice");
	}
}
