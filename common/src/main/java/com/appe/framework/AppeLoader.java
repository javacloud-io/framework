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
	 * Load all services from a resource properties file. NULL if resource not exist
	 * 
	 * module.class
	 * implementation.class
	 * interface.class=implementation/provider.class
	 * interface.class#named=implementation/provider.class
	 * other.modules
	 * 
	 * <named, interface, implementation>
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static List<Binding> loadBindings(String resource, ClassLoader loader)
			throws IOException, ClassNotFoundException {
		//NOT FOUND THE RESOURCE => NULL
		Properties properties = loadProperties(resource, loader);
		if(properties == null) {
			return null;
		}
		
		//LOAD ALL THE BINDINGS
		List<Binding> bindings = new ArrayList<Binding>();
		for(Enumeration<?> ename = properties.keys(); ename.hasMoreElements(); ) {
			String ztype = (String)ename.nextElement();
			String zimpl = properties.getProperty(ztype);
			
			//FIND MAPPING NAMED, using # to look for name
			Binding binding = new Binding();
			int idot = ztype.lastIndexOf('#');
			if(idot > 0) {
				binding.name = ztype.substring(idot + 1);
				ztype = ztype.substring(0, idot);
			}
			
			//NO IMPL => CHECK TO SEE IF BINDING IS PACKAGE
			if(zimpl == null || zimpl.isEmpty()) {
				idot = ztype.lastIndexOf('.');
				
				//DOESN'T LOOK LIKE CLASS NAME => ASSUMING PACKAGE NAME
				if(idot < 0 || idot >= ztype.length() || Character.isLowerCase(ztype.charAt(idot + 1))) {
					binding.name = ztype;
				} else {
					binding.typeClass = loader.loadClass(ztype);
				}
			} else {
				binding.typeClass = loader.loadClass(ztype);
				binding.implClass = loader.loadClass(zimpl);
			}
			bindings.add(binding);
		}
		return bindings;
	}
	
	/**
	 * Return a properties resource for given class loader. NULL indicate not FOUND the properties
	 * 
	 * @param resource
	 * @param loader
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Properties loadProperties(String resource, ClassLoader loader) throws IOException {
		//MAKE SURE USING DEFAULT ONE
		URL url = loader.getResource(resource);
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
	static final class OrderedProperties extends Properties {
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
	
	//BINDING INFO
	public static final class Binding {
		private String 	 name;
		private Class<?> typeClass;
		private Class<?> implClass;
		public Binding() {
		}
		public String name() {
			return name;
		}
		public Class<?> typeClass() {
			return typeClass;
		}
		public Class<?> implClass() {
			return implClass;
		}
	}
}
