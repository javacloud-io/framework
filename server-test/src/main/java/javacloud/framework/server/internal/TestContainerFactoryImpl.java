package javacloud.framework.server.internal;

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
public class TestContainerFactoryImpl implements TestContainerFactory {
	private TestContainerFactory delegate;
	private TestContainer container;
	
	public TestContainerFactoryImpl(TestContainerFactory delegate) {
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
