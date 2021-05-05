package javacloud.framework.server.internal;

import jakarta.ws.rs.core.Application;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
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
public abstract class JerseyApplicationTest extends JerseyTest {
	public static final String CONTAINER_PATH 		= "jersey.config.test.container.path";
	
	/**
	 * Enable the container debug by default. Turn off if needed to.
	 */
	public JerseyApplicationTest() {
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
		if(isTrafficLoggingEnabled()) {
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
		if(!(containerFactory instanceof JerseyTestContainerFactory)) {
			containerFactory = new JerseyTestContainerFactory(containerFactory);
		}
		return	containerFactory;
	}
	
	/**
	 * DISABLE FOLLOW REDIRECTS
	 */
	@Override
	protected void configureClient(ClientConfig config) {
		super.configureClient(config);
		config.property(ClientProperties.FOLLOW_REDIRECTS, false);
	}
	
	/**
	 * return the root context path, by default just using ROOT.
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
	protected boolean isTrafficLoggingEnabled() {
		return Boolean.valueOf(System.getProperty(TestProperties.LOG_TRAFFIC, "true"));
	}
}
