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
package com.appe.registry.cdi;

import com.appe.registry.AppeRegistry;
import com.google.inject.Injector;
import com.google.inject.Stage;

/**
 * Basic implementation using google juice and service override at runtime level. By default it will load a file:
 * META-INF/registry-modules.guice => then will perform overriding with XXX.1. It then can be perform a special override
 * using only a current class loading XXX.2.
 * 
 * @author ho
 *
 */
public class GuiceRegistryImpl extends AppeRegistry {
	private final Injector injector;
	/**
	 * 
	 */
	public GuiceRegistryImpl() {
		injector = createInjector();
	}
	
	/**
	 * return an instance of any service interface
	 * 
	 */
	@Override
	public final <T> T getInstance(Class<T> service) {
		return injector.getInstance(service);
	}

	/**
	 * For internal use only, can be call using reflection. Might be use for integration purpose..
	 * 
	 * @return
	 */
	public final Injector getInjector() {
		return injector;
	}
	
	/**
	 * By default we always looking for service with default profile unless it's override at runtime.
	 * 
	 * @return
	 */
	protected Injector createInjector() {
		String profile = System.getProperty(PROFILE, "guice");
		String resource= "META-INF/registry-modules." + profile;
		
		return GuiceFactory.createInjector(Stage.PRODUCTION, resource);
	}
}
