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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.appe.registry.AppeLoader;
import com.appe.registry.AppeRegistry;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.internal.Errors;

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
		ClassLoader loader = AppeLoader.getClassLoader();
		try {
			logger.info("Load modules from resource: " + resource);
			List<Module> modules = loadModules(AppeLoader.loadProperties(resource, true), loader);
			
			//ALWAYS MAKE SURE IT AT LEAST EMPTY
			if(modules == null) {
				modules = Collections.emptyList();
			}
			
			logger.info("Load override modules from resource: " + resource + ".1");
			List<Module> overrides = loadModules(AppeLoader.loadProperties(resource + ".1", true), loader);
			
			return builder.build(modules, overrides);
		} catch(IOException ex) {
			throw new ConfigurationException(Errors.getMessagesFromThrowable(ex));
		}
	}
	
	/**
	 * Load all the modules from properties file, using KEY as CLASSNAME. If module is not found => assuming OK!
	 * 
	 * @param props
	 * @param loader
	 * @return
	 */
	static List<Module> loadModules(Properties props, ClassLoader loader) {
		//NOTHING TO LOAD
		if(props == null || props.isEmpty()) {
			return null;
		}
		
		//LOAD ALL THE CLASS
		List<Module> modules = new ArrayList<Module>();
		for(String name: props.stringPropertyNames()) {
			logger.info("Load module class: " + name);
			try {
				Module m = (Module)loader.loadClass(name).newInstance();
				modules.add(m);
			} catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
				logger.warning("Unable to load module: " + name + ", reason: " + ex.getMessage());
			}
		}
		return modules;
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
