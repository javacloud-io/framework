package javacloud.framework.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;

import javacloud.framework.util.Objects;

/**
 * Basic jersey application configuration, providing basic features...
 * 
 * 1. Setup Guice integration
 * 2. Setup Jackson binder
 * 3. Default exception mapping
 * 
 * The default components is located under: META-INF/javacloud.server.components
 * 
 * @author ho
 *
 */
//@ApplicationPath(context)
public abstract class ServerApplication extends ResourceConfig {
	private static final Logger logger = Logger.getLogger(ServerApplication.class.getName());
	
	/**
	 * Configure how the resource should be combine, object should be inject...
	 * 
	 * @param serviceLocator
	 */
	public ServerApplication(String...packages) {
		configure(packages);
	}
	
	/**
	 * Default server configuration with Guice HK2 & JSON
	 * java.lange.Package
	 * @param packages
	 */
	protected void configure(String...packages) {
		//MAKE SURE IF AUTO DISCOVERRY IS DISABLED?
		if (!isAutoDiscovery()) {
			property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE_SERVER, true);
		}
		
		//PACKAGE CONFIG IF ANY
		if (!Objects.isEmpty(packages)) {
			packages(packages);
		}
		
		//AUTO LOAD THE COMPONENTS
		List<?> components = serverComponents();
		logger.log(Level.FINE, "Register jersey server components: {0}", components);
		
		for(Object c: components) {
			if(c instanceof Class) {
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
	
	/**
	 * javacloud.framework.server.impl.GuiceHK2Feature
	 * javacloud.framework.server.impl.JacksonFeature
	 * javacloud.framework.server.impl.GenericExceptionMapper
	 * 
	 * org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
	 * @return packages/objects/classes
	 */
	protected abstract List<?> serverComponents();
}
