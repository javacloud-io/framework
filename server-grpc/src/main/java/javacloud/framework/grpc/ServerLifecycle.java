package javacloud.framework.grpc;

import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import javacloud.framework.util.LazySupplier;

public abstract class ServerLifecycle extends LazySupplier<Server> {
	
	public void start() throws Exception {
		Server server = get();
		server.start();
	}
	
	public void stop() throws Exception {
		if (isInstantiated()) {
			Server server = get();
			server.shutdown();
			
			// wait until termination
			while (!server.isTerminated()) {
				server.awaitTermination(5, TimeUnit.SECONDS);
			}
		}
	}
}
