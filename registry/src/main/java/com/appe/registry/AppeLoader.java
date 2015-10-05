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
package com.appe.registry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;


/**
 * Simple utils to load thing such as class/resources...We always use current ClassLoader to allow override.
 * For some reasons you wish the behavior of class loading different, then just set to the current thread.
 * 
 * @author ho
 *
 */
public final class AppeLoader {
	private AppeLoader() {
	}
	
	/**
	 * Always favor to current thread class loader, by doing so most of the resource can be override.
	 * By using Thread.currentThread().setContextClassLoader(...)
	 * 
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(loader == null) {
			loader = AppeLoader.class.getClassLoader();
		}
		return loader;
	}
	
	/**
	 * Using java service loader to dynamically load one service. The implementation of class have to locate under:
	 * META-INF/services/[service.class.getName()]
	 * 
	 * @param service
	 * @return
	 */
	public static <T> T loadService(Class<T> service) {
		ServiceLoader<T> loader = ServiceLoader.load(service, getClassLoader());
		return loader.iterator().next();
	}
	
	/**
	 * Return all the classes from resource
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static List<Class<?>> loadClasses(String resource)
			throws IOException, ClassNotFoundException {
		//LOAD THE PROPERTIES
		Properties props = loadProperties(resource);
		if(props == null) {
			return null;
		}
		
		//LOAD ALL THE CLASSES
		ClassLoader loader = getClassLoader();
		List<Class<?>> zclasses = new ArrayList<Class<?>>();
		for(String name: props.stringPropertyNames()) {
			Class<?> zclass = loader.loadClass(name);
			zclasses.add(zclass);
		}
		return zclasses;
	}
	
	/**
	 * Load all services from a resource properties file. NULL if resource not exist
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> loadServices(String resource)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		//LOAD CLASSES
		List<Class<?>> zclasses = loadClasses(resource);
		if(zclasses == null) {
			return null;
		}
		
		//LOAD ALL THE CLASSES
		List<T> services = new ArrayList<T>();
		for(Class<?> zclass: zclasses) {
			T s = (T)zclass.newInstance();
			services.add(s);
		}
		return services;
	}
	
	/**
	 * Return a properties resource for given class loader.
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Properties loadProperties(String resource) throws IOException {
		//MAKE SURE USING DEFAULT ONE
		URL url = getClassLoader().getResource(resource);
		if(url == null) {
			return null;
		}
		
		//LOAD THE PROPERTIES
		Properties props = new Properties();
		try (InputStream stream = url.openStream()) {
			props.load(stream);
		}
		return props;
	}
}
