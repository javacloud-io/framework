package com.appe.framework.server;

import java.util.List;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.hk2.ComponentFactory;
/**
 * Basic jersey application configuration, providing basic features...
 * 
 * 1. Setup Guice integration
 * 2. Setup Jackson binder
 * 3. Default exception mapping
 * 
 * The default components is located under: META-INF/server-components.jersey
 * 
 * @author ho
 *
 */
//@ApplicationPath(context)
public class RestApplication extends ResourceConfig {
	private static final Logger logger = LoggerFactory.getLogger(RestApplication.class);
	
	/**
	 * Configure how the resource should be combine, object should be inject...
	 * 
	 * @param serviceLocator
	 */
	public RestApplication(String...packages) {
		configure(packages);
	}
	
	/**
	 * Default server configuration with Guice HK2 & JSON
	 * java.lange.Package
	 * @param packages
	 */
	protected void configure(String...packages) {
		//MAKE SURE IF AUTO DISCOVERRY IS DISABLED?
		if(!isAutoDiscovery()) {
			property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE_SERVER, true);
		}
		
		//PACKAGE CONFIG IF ANY
		if(packages != null && packages.length > 0) {
			packages(packages);
		}
		
		//AUTO LOAD THE COMPONENTS
		List<Object> components = ComponentFactory.loadComponents("META-INF/server-components.jersey");
		logger.debug("Registering jersey server components: {}", components);
		for(Object c: components) {
			if( c instanceof Class) {
				register((Class<?>)c);
			} else if(c instanceof Package) {
				packages(((Package)c).getName());
			} else {
				register(c);
			}
		}
	}
	
	/**
	 * Disabled the auto discovery of the feature to avoid unexpected behavior.
	 * 
	 * @return
	 */
	protected boolean isAutoDiscovery() {
		return false;
	}
}
