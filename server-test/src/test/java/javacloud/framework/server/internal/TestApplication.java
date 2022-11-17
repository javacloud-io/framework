package javacloud.framework.server.internal;

import jakarta.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
/**
 * 
 * @author ho
 *
 */
@ApplicationPath("/v1")
public class TestApplication extends ResourceConfig {
	public TestApplication() {
		packages("javacloud.framework.server.rest");
	}
}
