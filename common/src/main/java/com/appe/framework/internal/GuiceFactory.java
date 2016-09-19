package com.appe.framework.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.inject.Provider;

import com.appe.framework.AppeLoader;
import com.appe.framework.AppeRegistry;
import com.appe.framework.util.Objects;
import com.appe.framework.util.Pair;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
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
	 * Properties file with modules/services implementation
	 * 
	 * # module.class
	 * # implementation.class
	 * # interface.class=implementation/provider.class
	 * # interface.class:named=implementation/provider.class
	 * 
	 * @param resource
	 * @return
	 */
	private static List<Module> loadModules(String resource) {
		try {
			Properties properties = AppeLoader.loadProperties(resource);
			if(Objects.isEmpty(properties)) {
				return Objects.asList();
			}
			
			ClassLoader loader = AppeLoader.getClassLoader();
			List<Module> zmodules = new ArrayList<>();
			final List<Pair<Pair<Class<?>, String>, Class<?>>> zservices = new ArrayList<>();
			for(Enumeration<?> ename = properties.keys(); ename.hasMoreElements(); ) {
				String ztype = (String)ename.nextElement();
				String zimpl = properties.getProperty(ztype);
				
				//FIND MAPPING NAMED
				String zname;
				int idot = ztype.indexOf(':');
				if(idot > 0) {
					zname = ztype.substring(idot + 1);
					ztype = ztype.substring(0, idot);
				} else {
					zname = null;
				}
				
				//LOAD ALL MODULES/SERVICES
				Class<?> zclass = loader.loadClass(ztype);
				if(Module.class.isAssignableFrom(zclass)) {
					zmodules.add((Module)zclass.newInstance());
				} else {
					Class<?> zzimpl;
					if(!Objects.isEmpty(zimpl)) {
						zzimpl = loader.loadClass(zimpl);
					} else {
						zzimpl = null;
					}
					zservices.add(new Pair<Pair<Class<?>, String>, Class<?>>(new Pair<Class<?>, String>(zclass, zname), zzimpl));
				}
			}
			
			//CONSTRUCT DYNAMIC MODULE AT THE END!!!
			if(!zservices.isEmpty()) {
				GuiceModule dynamicModule = new GuiceModule() {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					protected void configure() {
						for(Pair<Pair<Class<?>, String>, Class<?>> pair: zservices) {
							Pair<Class<?>, String> zclass = pair.getKey();
							String zname = zclass.getValue();
							
							LinkedBindingBuilder<?> binding;
							if(Objects.isEmpty(zname)) {
								binding = bind(zclass.getKey());
							} else {
								binding = bindNamed(zclass.getKey(), zname);
							}
							
							//BIND TO IMPL IF VALID
							Class<?> zzimpl = pair.getValue();
							if(zzimpl != null) {
								if(Provider.class.isAssignableFrom(zzimpl)) {
									binding.toProvider((Class)zzimpl);
								} else {
									binding.to((Class)zzimpl);
								}
							}
						}
					}
				};
				zmodules.add(dynamicModule);
			}
			return zmodules;
		} catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new ConfigurationException(Arrays.asList(new Message(ex.getClass() + ": " + ex.getMessage())));
		}
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
		logger.info("Loading modules from resource: " + resource);
		List<Module> modules = loadModules(resource);
		
		//ALWAYS MAKE SURE IT AT LEAST EMPTY
		if(modules == null) {
			modules = Collections.emptyList();
		}
		
		logger.info("Load override modules from resource: " + resource + ".1");
		List<Module> overrides = loadModules(resource + ".1");
		
		return builder.build(modules, overrides);
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
	 * 
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
