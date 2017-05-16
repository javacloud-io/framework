package com.appe.framework.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Provider;

import com.appe.framework.AppeLoader;
import com.appe.framework.AppeRegistry;
import com.appe.framework.util.Objects;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.spi.Message;

/**
 * Helper class to load and create juice injector. By default it will scan for registry-services.guice properties file.
 * 
 * #
 * # module.class
 * # implementation.class
 * # interface.class=implementation/provider.class
 * # interface.class#named=implementation/provider.class
 * # other modules
 * #
 * 
 * @author ho
 *
 */
public final class GuiceFactory {
	private static final Logger logger = Logger.getLogger(GuiceFactory.class.getName());
	
	static final String MAIN_RESOURCE	= "META-INF/registry-services.guice";
	static final String SUB_RESOURCE 	= "META-INF/registry/";
	private GuiceFactory() {
	}
	
	/**
	 * Properties file with modules/services implementation
	 * 
	 * 
	 * @param resource
	 * @param loader
	 * @return
	 */
	private static List<Module> loadModules(String resource, ClassLoader loader) {
		try {
			List<AppeLoader.Binding> bindings = AppeLoader.loadBindings(resource, loader);
			if(Objects.isEmpty(bindings)) {
				logger.fine("Not found modules or resource file: " + resource);
				return Objects.asList();
			}
			
			final List<Module> zmodules = new ArrayList<>();
			final List<AppeLoader.Binding> zservices = new ArrayList<>();
			for(AppeLoader.Binding binding: bindings) {
				Class<?> typeClass = binding.typeClass();
				
				// EMPTY TYPE => ASSUMING THIS IS LINK TO OTHERS
				if(typeClass == null && binding.implClass() == null) {
					String subresource = SUB_RESOURCE + binding.name();
					
					logger.fine("Including modules from resource file: " + subresource);
					zmodules.addAll(loadModules(subresource, loader));
				} else {
					//LOAD ALL MODULES/SERVICES
					if(Module.class.isAssignableFrom(typeClass)) {
						logger.fine("Registering module: " + typeClass);
						zmodules.add((Module)typeClass.newInstance());
					} else {
						zservices.add(binding);
					}
				}
			}
			
			//CONSTRUCT DYNAMIC MODULE AT THE END!!!
			if(!zservices.isEmpty()) {
				GuiceModule dynamicModule = new GuiceModule() {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					protected void configure() {
						for(AppeLoader.Binding binding: zservices) {
							LinkedBindingBuilder<?> bindingBuilder;
							if(Objects.isEmpty(binding.name())) {
								bindingBuilder = bind(binding.typeClass());
							} else {
								bindingBuilder = bindNamed(binding.typeClass(), binding.name());
							}
							
							//BIND TO IMPL IF VALID
							Class<?> implClass = binding.implClass();
							if(implClass != null) {
								if(Provider.class.isAssignableFrom(implClass)) {
									bindingBuilder.toProvider((Class)implClass);
								} else {
									bindingBuilder.to((Class)implClass);
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
	 * 
	 * @param builder
	 * @param loader
	 * 
	 * @return
	 */
	public static Injector createInjector(GuiceBuilder builder, ClassLoader loader) {
		return createInjector(builder, MAIN_RESOURCE, loader);
	}
	
	/**
	 * 1. Load all the modules from all classes with resource
	 * 2. Load all the modules override with resource.1
	 * 
	 * @param builder
	 * @param resource
	 * @param loader
	 * @return
	 */
	public static Injector createInjector(GuiceBuilder builder, String resource, ClassLoader loader) {
		logger.info("Loading modules from resource file: " + resource);
		List<Module> modules = loadModules(resource, loader);
		
		//ALWAYS MAKE SURE IT AT LEAST EMPTY
		if(modules == null) {
			modules = Collections.emptyList();
		}
		return builder.build(modules, null);
	}
	
	/**
	 * HACK TO GET BACK INJECTOR from registry to avoid circular dependency
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
