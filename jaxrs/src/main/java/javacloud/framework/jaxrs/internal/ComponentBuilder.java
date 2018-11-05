package javacloud.framework.jaxrs.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;

/**
 * Utils to load all the components from resource + support reference + linke to sub resource
 * 
 * @author ho
 *
 */
public class ComponentBuilder {
	private static final Logger logger = Logger.getLogger(ComponentBuilder.class.getName());
	public ComponentBuilder() {
	}
	
	/**
	 * return all the components + dependencies from resource. A components can be an instance of something
	 * or class or string represent component
	 * 
	 * @param resource
	 * @param loader
	 * @return
	 */
	public List<?> build(String resource, ClassLoader loader) {
		try {
			List<ResourceLoader.Binding> bindings = ResourceLoader.loadBindings(resource, loader);
			if(Objects.isEmpty(bindings)) {
				logger.log(Level.FINE, "Not found components or resource: {0}", resource);
				return Objects.asList();
			}
			//
			return build(bindings, loader);
		} catch(IOException | ClassNotFoundException ex) {
			throw Exceptions.asUnchecked(ex);
		}
	}
	
	/**
	 * 
	 * @param bindings
	 * @param loader
	 * @return
	 */
	public List<?> build(List<ResourceLoader.Binding> bindings, ClassLoader loader) {
		final List<Object> components = new ArrayList<>();
		for(ResourceLoader.Binding binding: bindings) {
			Class<?> typeClass = binding.typeClass();
			
			// EMPTY TYPE => ASSUMING THIS IS LINK TO OTHERS
			if(typeClass == null && binding.implClass() == null) {
				Package pkg = Package.getPackage(binding.name());
				if(pkg == null) {
					String subresource = ResourceLoader.META_INF + binding.name();
					logger.log(Level.FINE, "Including components from resource file: {0}", subresource);
					components.addAll(build(subresource, loader));
				} else {
					logger.log(Level.FINE, "Including components from package: {0}", pkg);
					components.add(pkg);
				}
			} else {
				components.add(binding.typeClass());
			}
		}
		return components;
	}
}
