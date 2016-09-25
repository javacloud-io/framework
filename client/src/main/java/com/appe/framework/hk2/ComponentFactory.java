package com.appe.framework.hk2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.appe.framework.AppeException;
import com.appe.framework.AppeLoader;
import com.appe.framework.util.Objects;

/**
 * Utils to load all the components from resource + support reference + linke to sub resource
 * 
 * @author ho
 *
 */
public final class ComponentFactory {
	private static final Logger logger = Logger.getLogger(ComponentFactory.class.getName());
	static final String SUB_RESOURCE 	= "META-INF/jersey/";
	
	private ComponentFactory() {
	}
	
	/**
	 * return all the components + dependencies from resource. A components can be an instance of something
	 * or class or string represent component
	 * 
	 * @param resource
	 * @return
	 */
	public static List<Object> loadComponents(String resource) {
		try {
			ClassLoader loader = AppeLoader.getClassLoader();
			List<AppeLoader.Binding> bindings = AppeLoader.loadBindings(resource, loader);
			if(Objects.isEmpty(bindings)) {
				logger.fine("Not found components or resource file: " + resource);
				return Objects.asList();
			}
			
			final List<Object> zcomponents = new ArrayList<>();
			for(AppeLoader.Binding binding: bindings) {
				Class<?> typeClass = binding.typeClass();
				
				// EMPTY TYPE => ASSUMING THIS IS LINK TO OTHERS
				if(typeClass == null && binding.implClass() == null) {
					Package pkg = Package.getPackage(binding.name());
					if(pkg == null) {
						String subresource = SUB_RESOURCE + binding.name();
						logger.fine("Including components from resource file: " + subresource);
						zcomponents.addAll(loadComponents(subresource));
					} else {
						logger.fine("Including components from package: " + pkg);
						zcomponents.add(pkg);
					}
				} else {
					zcomponents.add(binding.typeClass());
				}
			}
			return zcomponents;
		} catch(IOException | ClassNotFoundException ex) {
			throw AppeException.wrap(ex);
		}
	}
}
