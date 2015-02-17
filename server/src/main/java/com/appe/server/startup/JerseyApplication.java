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

import org.glassfish.jersey.server.ResourceConfig;

import com.appe.server.hk2.GuiceHK2Feature;
import com.appe.server.hk2.JacksonContextResolver;
/**
 * Basic jersey application configuration, providing basic features...
 * 
 * 1. Setup Guice integration
 * 2. Setup Jackson binder
 * 
 * @author ho
 *
 */
//@ApplicationPath(context)
public class JerseyApplication extends ResourceConfig {
	/**
	 * Configure how the resource should be combine, object should be inject...
	 * @param serviceLocator
	 */
	public JerseyApplication(String...packages) {
		if(packages != null && packages.length > 0) {
			packages(packages);
		}
		
		//BASIC features
		register(GuiceHK2Feature.class);
		register(JacksonContextResolver.class);
		//register(RolesAllowedDynamicFeature.class);
	}
}
