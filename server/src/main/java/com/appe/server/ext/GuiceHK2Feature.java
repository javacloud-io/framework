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
package com.appe.server.ext;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.registry.spi.GuiceFactory;
import com.google.inject.Injector;
/**
 * If the registry is GuiceRegistry => try to find it and register with the system
 * 
 * @author ho
 *
 */
public class GuiceHK2Feature implements Feature {
	private static final Logger logger = LoggerFactory.getLogger(GuiceHK2Feature.class);
	private ServiceLocator serviceLocator;
	/**
	 * 
	 * @param serviceLocator
	 */
	@Inject
	public GuiceHK2Feature(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	/**
	 * Trying to find the injector already initialize somewhere and register if FOUND
	 * 
	 */
	@Override
	public boolean configure(FeatureContext context) {
		Injector injector = GuiceFactory.registryInjector();
		if(injector == null) {
			return false;
		}
		
		logger.info("Register Guice bridge to HK2");
		GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
		GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
		guiceBridge.bridgeGuiceInjector(injector);
		return true;
	}
}
