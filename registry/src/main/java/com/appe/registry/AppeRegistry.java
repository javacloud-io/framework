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
package com.appe.registry;

import java.util.List;


/**
 * Basic registry operation for lookup & register object, module...
 * 
 * @author ho
 *
 */
public abstract class AppeRegistry {
	public static final  String PROFILE		= "com.appe.registry.profile";
	private static final AppeRegistry APPE 	= AppeLoader.loadService(AppeRegistry.class);
	
	protected AppeRegistry() {
	}
	
	/**
	 * return the only instance available
	 * 
	 * @return
	 */
	public static final AppeRegistry get() {
		return APPE;
	}
	
	/**
	 * Get class instance from registry
	 * 
	 * @param service
	 * @return
	 */
	public abstract <T> T getInstance(Class<T> service);
	
	/**
	 * Get instance using @Named
	 * 
	 * @param service
	 * @param name
	 * @return
	 */
	public abstract <T> T getInstance(Class<T> service, String name);
	
	/**
	 * return all instances of the given service type, intended for plugin and extension. Plugin should bind
	 * with random name
	 * 
	 * @param service
	 * @return
	 */
	public abstract <T> List<T> getInstances(Class<T> service);
	
	/**
	 * Configuration instance by default just annotated using NAME=VALUE, with optional bundle
	 * 
	 * @param config
	 * @return
	 */
	public <T> T getConfig(Class<T> config) {
		AppeConfig factory = getInstance(AppeConfig.class);
		return factory.get(config);
	}
}
