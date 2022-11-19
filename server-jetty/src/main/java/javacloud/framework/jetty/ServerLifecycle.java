package javacloud.framework.jetty;

import org.eclipse.jetty.server.Server;

import javacloud.framework.util.LazySupplier;

public abstract class ServerLifecycle extends LazySupplier<Server> {
	
	public void start() throws Exception {
		Server server = get();
		server.start();
	}
	
	public void stop() throws Exception {
		if (isInstantiated()) {
			Server server = get();
			server.stop();
		}
	}
	
}
