package com.appe.server.test;

import java.net.URI;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerFactory;

//RESPECT THE PATH ANNOTATION
public class DelegateTestContainerFactory implements TestContainerFactory {
	private TestContainerFactory delegate;
	public DelegateTestContainerFactory(TestContainerFactory delegate) {
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