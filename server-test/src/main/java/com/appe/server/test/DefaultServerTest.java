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
package com.appe.server.test;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import com.appe.server.test.internal.DelegateTestContainerFactory;

/**
 * To be able to separate the client baseURI and serverURI. Most of the time we would like the URI of test client to look
 * similar in the production. Jersey doesn't support that kind of stuff.
 * 
 * Using system properties to which to which kind of server factory one would like to use: Grizzly, In-Memory, JDK, Simple HTTP, Jetty.
 * TestProperties.CONTAINER_FACTORY
 * 
 * For details debug purpose:
 * enable(TestProperties.LOG_TRAFFIC);
 * enable(TestProperties.DUMP_ENTITY)
 * 
 * Override configureDeployment for different context root.
 * 
 * @author ho
 *
 */
public abstract class DefaultServerTest extends JerseyTest {
	public static final String CONTAINER_PATH 		= "jersey.config.test.container.path";
	public static final String CONTAINER_DEBUG		= "jersey.config.test.container.debug";
	
	/**
	 * Enable the container debug by default. Turn off if needed to.
	 */
	public DefaultServerTest() {
		enable(CONTAINER_DEBUG);
	}

	/**
	 * Make the subclass be aware of override to configure the server.
	 */
	@Override
	protected abstract Application configure();
	
	/**
	 * Always turn on TRAFFIC LONG & DUMP can be override via system properties
	 */
	@Override
	protected DeploymentContext configureDeployment() {
		if(isDebugContainer()) {
			enable(TestProperties.LOG_TRAFFIC);
			enable(TestProperties.DUMP_ENTITY);
		}
		
		return DeploymentContext.builder(configure())
					.contextPath(getContainerPath()).build();
	}
	
	/**
	 * Always give back test container which suitable to use client absolute URI instead of 
	 * 
	 */
	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		TestContainerFactory containerFactory = super.getTestContainerFactory();
		if(!(containerFactory instanceof DelegateTestContainerFactory)) {
			containerFactory = new DelegateTestContainerFactory(containerFactory);
		}
		return	containerFactory;
	}
	
	/**
	 * return the root context path
	 * 
	 * @return
	 */
	protected String getContainerPath() {
		return	System.getProperty(CONTAINER_PATH, "");
	}
	
	/**
	 * To dump log & entity to log stream.
	 * 
	 * @return
	 */
	protected boolean isDebugContainer() {
		return Boolean.valueOf(System.getProperty(CONTAINER_DEBUG, "true"));
	}
}
