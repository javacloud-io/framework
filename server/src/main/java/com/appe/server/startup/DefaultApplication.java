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
package com.appe.server.startup;

import java.io.IOException;
import java.util.List;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.registry.AppeLoader;
/**
 * Basic jersey application configuration, providing basic features...
 * 
 * 1. Setup Guice integration
 * 2. Setup Jackson binder
 * 3. Default exception mapping
 * 
 * @author ho
 *
 */
//@ApplicationPath(context)
public class DefaultApplication extends ResourceConfig {
	private static final Logger logger = LoggerFactory.getLogger(DefaultApplication.class);
	
	/**
	 * Configure how the resource should be combine, object should be inject...
	 * 
	 * @param serviceLocator
	 */
	public DefaultApplication(String...packages) {
		configure(packages);
	}
	
	/**
	 * Default server configuration with Guice HK2 & JSON
	 * 
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
		try {
			List<Class<?>> components = AppeLoader.loadClasses("META-INF/server-components.jersey");
			logger.debug("Register jersey components: {}", components);
			for(Class<?> component: components) {
				register(component);
			}
		}catch(IOException | ClassNotFoundException ex) {
			//DON'T RE-THROW EXCEPTION B/C IT's NOT SOLVING ANY REAL PROBLEM
			logger.error("Unable to load jersey components", ex);
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
