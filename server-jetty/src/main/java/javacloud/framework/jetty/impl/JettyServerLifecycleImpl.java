package javacloud.framework.jetty.impl;

import java.net.InetSocketAddress;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import jakarta.ws.rs.ApplicationPath;
import javacloud.framework.config.ConfigManager;
import javacloud.framework.jetty.ServerLifecycle;
import javacloud.framework.jetty.ServerSettings;
import javacloud.framework.server.ServerApplication;

@Singleton
public class JettyServerLifecycleImpl extends ServerLifecycle {
	private final ServerApplication application;
	private final ServerSettings settings;
	
	@Inject
	public JettyServerLifecycleImpl(ServerApplication application, ConfigManager configManager) {
		this(application, configManager.getConfig(ServerSettings.class));
	}
	
	protected JettyServerLifecycleImpl(ServerApplication application, ServerSettings settings) {
		this.application = application;
		this.settings = settings;
	}
	
	@Override
	protected Server newInstance() {
		// binding address
		InetSocketAddress socketAddress = new InetSocketAddress(
				settings.serverAddress(),
				settings.serverPort());
		Server server = new Server(socketAddress);
		
		// context handler
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		handler.setContextPath(settings.contextPath());
		
		// application path
		String pathSpec;
		ApplicationPath ctxPath = application.getClass().getAnnotation(ApplicationPath.class);
		if (ctxPath != null) {
			pathSpec = ctxPath.value();
			if (pathSpec == null || pathSpec.isEmpty()) {
				pathSpec = "/*";
			} else if (!pathSpec.endsWith("/*")) {
				pathSpec += "/*";
			}
		} else {
			pathSpec = "/*";
		}
		
		handler.addServlet(new ServletHolder(new ServletContainer(application)), pathSpec);
        server.setHandler(handler);
        
		return server;
	}
	
	public void awaitTermination() throws Exception {
		Server server = get();
		server.join();
	}
}
