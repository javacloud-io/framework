package com.appe.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;


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
	 * Return all the classes from given resource
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
		for(Enumeration<?> ename = props.keys(); ename.hasMoreElements(); ) {
			Class<?> zclass = loader.loadClass((String)ename.nextElement());
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
		
		//LOAD THE PROPERTIES & MAKE SURE KEEP KEY ORDERED
		Properties props = new OrderedProperties();
		try (InputStream stream = url.openStream()) {
			props.load(stream);
		}
		return props;
	}
	
	//MAKE SURE LOADED KEYS IS CORRECT ORDER IF ENUMERATE BY KEYS!!!
	static class OrderedProperties extends Properties {
		private static final long serialVersionUID = 1L;
		private final Set<Object> keys = new LinkedHashSet<Object>();
		//ORDER KEYS
		@Override
		public synchronized Enumeration<Object> keys() {
			return Collections.enumeration(keys);
		}
		//ORDER KEYS
		@Override
		public Set<Object> keySet() {
			return keys;
		}
		@Override
		public synchronized Object put(Object key, Object value) {
			keys.add(key);
			return super.put(key, value);
		}
		@Override
		public synchronized Object remove(Object key) {
			keys.remove(key);
			return super.remove(key);
		}
	}
}
