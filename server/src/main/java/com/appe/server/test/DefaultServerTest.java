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

import java.net.URI;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

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
	public static final String CONTAINER_CONTEXT = "jersey.config.test.container.context";
	public static final String CONTAINER_DEBUG	 = "jersey.config.test.container.debug";
	
	public DefaultServerTest() {
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
		//TURN ON DEBUG BY DEFAULT
		boolean debug = Boolean.valueOf(System.getProperty(CONTAINER_DEBUG, "true"));
		if(debug) {
			enable(TestProperties.LOG_TRAFFIC);
			enable(TestProperties.DUMP_ENTITY);
		}
		
		String contextPath = System.getProperty(CONTAINER_CONTEXT, "");
		return DeploymentContext.builder(configure())
				.contextPath(contextPath).build();
	}
	
	/**
	 * Always give back test container which suitable to use client absolute URI instead of 
	 * 
	 */
	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		TestContainerFactory containerFactory = super.getTestContainerFactory();
		if(!(containerFactory instanceof TestContainerFactoryImpl)) {
			containerFactory = new TestContainerFactoryImpl(containerFactory);
		}
		return	containerFactory;
	}

	//RESPECT THE PATH ANNOTATION
	static class TestContainerFactoryImpl implements TestContainerFactory {
		private TestContainerFactory delegate;
		public TestContainerFactoryImpl(TestContainerFactory delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public TestContainer create(URI baseUri, DeploymentContext context) {
			//JerseyTest always build URI with ROOT context path as default, so we can just re-struct full server annotation
			UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
			ApplicationPath path = context.getResourceConfig().getClass().getAnnotation(ApplicationPath.class);
			if(path != null) {
				String base = path.value();
				if(base.endsWith("/*")) {
					base = base.substring(0, base.length() - 2);
				}
				uriBuilder.path(base);
			}
			return new TestContainerImpl(baseUri, delegate.create(uriBuilder.build(), context));
		}
	}
	
	//Make sure to USE the client URI after server is started
	static class TestContainerImpl implements TestContainer {
		private URI baseUri;
		private TestContainer delegate;
		public TestContainerImpl(URI baseUri, TestContainer delegate) {
			this.baseUri  = baseUri;
			this.delegate = delegate;
		}
		
		@Override
		public ClientConfig getClientConfig() {
			return delegate.getClientConfig();
		}

		@Override
		public URI getBaseUri() {
			return	baseUri;
		}

		@Override
		public void start() {
			delegate.start();
		}

		@Override
		public void stop() {
			delegate.stop();
		}
	}
}
