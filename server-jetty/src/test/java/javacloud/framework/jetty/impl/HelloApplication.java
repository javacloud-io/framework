package javacloud.framework.jetty.impl;

import jakarta.ws.rs.ApplicationPath;
import javacloud.framework.server.ServerApplication;

import java.util.Collections;
import java.util.List;
/**
 * 
 * @author ho
 *
 */
@ApplicationPath("/v1")
public class HelloApplication extends ServerApplication {
	public HelloApplication() {
		super("javacloud.framework.jetty.api");
	}

	@Override
	protected List<?> serverComponents() {
		return Collections.emptyList();
	}
}
