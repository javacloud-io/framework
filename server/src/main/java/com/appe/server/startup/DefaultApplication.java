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

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;

import com.appe.server.ext.GuiceHK2Feature;
import com.appe.server.ext.JacksonContextResolver;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
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
	/**
	 * Configure how the resource should be combine, object should be inject...
	 * 
	 * @param serviceLocator
	 */
	public DefaultApplication(String...packages) {
		configure(packages);
	}
	
	/**
	 * Default server configuration
	 * @param packages
	 */
	protected void configure(String...packages) {
		property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE_SERVER, true);
		property(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE_SERVER, true);
		if(packages != null && packages.length > 0) {
			packages(packages);
		}
		
		//GUICE HK2
		register(GuiceHK2Feature.class);
		
		//JACKSON POJO
		register(JacksonContextResolver.class);
		register(JacksonJaxbJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
		
		//register(RolesAllowedDynamicFeature.class);
		register(ThrowableExceptionMapper.class);
	}
}
