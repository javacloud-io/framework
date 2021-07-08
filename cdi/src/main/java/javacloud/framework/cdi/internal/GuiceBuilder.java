package javacloud.framework.cdi.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Provider;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.spi.Message;
import com.google.inject.util.Modules;

import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;
/**
 * Delegate in what form we would like to build injector
 * 
 * @author ho
 *
 */
public abstract class GuiceBuilder {
	private static final Logger logger = Logger.getLogger(GuiceBuilder.class.getName());
	
	protected GuiceBuilder() {
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
	public Injector build(String resource, ClassLoader loader) {
		logger.fine("Register service modules from resource: " + resource);
		List<Module> modules = loadModules(resource, loader);
		
		//ALWAYS MAKE SURE IT AT LEAST EMPTY
		if (modules == null) {
			modules = Collections.emptyList();
		}
		return build(modules, null);
	}
	
	/**
	 * Build an injector from set of modules and overrides
	 * 
	 * @param modules
	 * @param overrides
	 * @return
	 */
	public abstract Injector build(List<Module> modules, List<Module> overrides);
	
	/**
	 * Properties file with modules/services implementation
	 * 
	 * @param resource
	 * @param loader
	 * @return
	 */
	protected List<Module> loadModules(String resource, ClassLoader loader) {
		try {
			List<ResourceLoader.Binding> bindings = ResourceLoader.loadBindings(resource, loader);
			if (Objects.isEmpty(bindings)) {
				logger.fine("Not found modules or resource file: " + resource);
				return Objects.asList();
			}
			
			final List<Module> zmodules = new ArrayList<>();
			final List<ResourceLoader.Binding> zservices = new ArrayList<>();
			for (ResourceLoader.Binding binding: bindings) {
				Class<?> typeClass = binding.typeClass();
				
				// EMPTY TYPE => ASSUMING THIS IS LINK TO OTHERS
				if(typeClass == null && binding.implClass() == null) {
					String subresource = ResourceLoader.META_INF + binding.name();
					
					logger.fine("Including modules from resource file: " + subresource);
					zmodules.addAll(loadModules(subresource, loader));
				} else {
					//LOAD ALL MODULES/SERVICES
					if(Module.class.isAssignableFrom(typeClass)) {
						logger.fine("Registering module: " + typeClass);
						zmodules.add((Module)typeClass.getConstructor().newInstance());
					} else {
						zservices.add(binding);
					}
				}
			}
			
			//CONSTRUCT DYNAMIC MODULE AT THE END!!!
			if (!zservices.isEmpty()) {
				GuiceModule dynamicModule = new GuiceModule() {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					protected void configure() {
						for (ResourceLoader.Binding binding: zservices) {
							LinkedBindingBuilder<?> bindingBuilder;
							
							if (Objects.isEmpty(binding.name())) {
								bindingBuilder = bind(binding.typeClass());
							} else {
								bindingBuilder = bindToName(binding.typeClass(), binding.name());
							}
							
							//BIND TO IMPL/PROVIDER IF VALID
							Class<?> implClass = binding.implClass();
							if (implClass != null) {
								if (Provider.class.isAssignableFrom(implClass)) {
									bindingBuilder.toProvider((Class)implClass);
								} else {
									bindingBuilder.to((Class)implClass);
								}
							}
							
							//always using singleton
							bindingBuilder.in(Scopes.SINGLETON);
						}
					}
				};
				zmodules.add(dynamicModule);
			}
			return zmodules;
		} catch(Exception ex) {
			throw new ConfigurationException(Arrays.asList(new Message(ex.getClass() + ": " + ex.getMessage())));
		}
	}
	
	//
	//IMPLEMENT BUILDER BY STAGE
	public static class StageBuilder extends GuiceBuilder {
		private final Stage stage;
		
		public StageBuilder(Stage stage) {
			this.stage = stage;
		}
		
		@Override
		public Injector build(List<Module> modules, List<Module> overrides) {
			if (overrides == null || overrides.isEmpty()) {
				return Guice.createInjector(stage, modules);
			}
			return Guice.createInjector(stage, Modules.override(modules).with(overrides));
		}
	}
	
	//
	//IMPLEMENT BUILDER BY PARENT
	public static class InheritBuilder extends GuiceBuilder {
		private Injector parent;
		
		public InheritBuilder(Injector parent) {
			this.parent = parent;
		}
		
		@Override
		public Injector build(List<Module> modules, List<Module> overrides) {
			if (overrides == null || overrides.isEmpty()) {
				return parent.createChildInjector(modules);
			}
			return parent.createChildInjector(Modules.override(modules).with(overrides));
		}
	}
}
