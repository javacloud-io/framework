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
package com.appe.server.test.internal;

import java.net.URI;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerFactory;
/**
 * Make sure the container always create ONLY ONE TIME. START ONE TIME AND NEVER BE SHUTDOWN.
 * 
 * @author ho
 *
 */
public class IntegrationTestContainerFactory implements TestContainerFactory {
	private TestContainerFactory delegate;
	private TestContainer container;
	public IntegrationTestContainerFactory(TestContainerFactory delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * Always return a wrapped version of the container.
	 */
	@Override
	public TestContainer create(URI baseUri, DeploymentContext deploymentContext) {
		if(container == null) {
			container = delegate.create(baseUri, deploymentContext);
			container.start();
		}
		return new TestContainerImpl(container);
	}
	
	/**
	 * Delegate to container STOP.
	 */
	public void shutdown() {
		if(container != null) {
			try {
				container.stop();
			} finally {
				container = null;
			}
		}
	}
	
	//MAKE SURE NOT EVER TO STOP AT ALL
	static class TestContainerImpl implements TestContainer {
		private TestContainer delegate;
		public TestContainerImpl(TestContainer delegate) {
			this.delegate = delegate;
		}
		@Override
		public ClientConfig getClientConfig() {
			return delegate.getClientConfig();
		}

		@Override
		public URI getBaseUri() {
			return delegate.getBaseUri();
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
		}
	}
}
