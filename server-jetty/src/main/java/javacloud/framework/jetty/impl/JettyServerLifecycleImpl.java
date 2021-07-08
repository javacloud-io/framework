package javacloud.framework.jetty.impl;

import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import javacloud.framework.jetty.ServerLifecycle;

@Singleton
public class JettyServerLifecycleImpl extends ServerLifecycle {

	@Override
	protected Server newInstance() {
		Server server = new Server(0);
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		servletHandler.setContextPath("/");
        server.setHandler(servletHandler);
		return server;
	}
	
}
