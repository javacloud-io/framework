package com.appe.framework.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.appe.framework.AppeLoader;
import com.appe.framework.AppeRegistry;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Message;

/**
 * Helper class to load and create juice injector. By default it will scan for appe-services.registry properties file.
 * 
 * @author ho
 *
 */
public final class GuiceFactory {
	private static final Logger logger = Logger.getLogger(GuiceFactory.class.getName());
	private GuiceFactory() {
	}
	
	/**
	 * 1. Load all the modules from all classes with resource
	 * 2. Load all the modules override with resource.1
	 * 
	 * @param builder
	 * @param resource
	 * @return
	 */
	public static Injector createInjector(GuiceBuilder builder, String resource) {
		try {
			logger.info("Loading modules from resource: " + resource);
			List<Module> modules = AppeLoader.loadServices(resource);
			
			//ALWAYS MAKE SURE IT AT LEAST EMPTY
			if(modules == null) {
				modules = Collections.emptyList();
			}
			
			logger.info("Load override modules from resource: " + resource + ".1");
			List<Module> overrides = AppeLoader.loadServices(resource + ".1");
			
			return builder.build(modules, overrides);
		} catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new ConfigurationException(Arrays.asList(new Message(ex.getClass() + ": " + ex.getMessage())));
		}
	}
	
	/**
	 * HACK TO GET BACK INJECTOR from registry:
	 * 
	 * Assuming the Registry return an INJECTOR which can be use for testing as well as other purpose
	 * 
	 * @return
	 */
	public static Injector registryInjector() {
		try {
			AppeRegistry registry = AppeRegistry.get();
			Method method = registry.getClass().getMethod("getInjector");
			return (Injector)method.invoke(registry);
		} catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			logger.warning("Unable to find Guice injector, reason: " + ex.getMessage());
		}
		return null;
	}
	
	/**
	 * Find all instances of the service type.
	 */
	public static <T> List<T> getInstances(Injector injector, Class<T> type) {
		List<Binding<T>> bindings = injector.findBindingsByType(TypeLiteral.get(type));
		if(bindings == null || bindings.isEmpty()) {
			return Collections.emptyList();
		}
		List<T> instances = new ArrayList<>(bindings.size());
		for(Binding<T> b: bindings) {
			instances.add(b.getProvider().get());
		}
		return instances;
	}
}