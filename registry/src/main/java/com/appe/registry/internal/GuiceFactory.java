/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.registry.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.appe.registry.AppeLoader;
import com.appe.registry.AppeRegistry;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Module;
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
	public final static Injector registryInjector() {
		try {
			AppeRegistry registry = AppeRegistry.get();
			Method method = registry.getClass().getMethod("getInjector");
			return (Injector)method.invoke(registry);
		} catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			logger.warning("Unable to find Guice injector, reason: " + ex.getMessage());
		}
		return null;
	}
}
