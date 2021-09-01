package javacloud.framework.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.ServerBootstrapConfig;
import javacloud.framework.util.LazySupplier;

public abstract class ServerLifecycle extends LazySupplier<ServerBootstrap> {
	
	public void start() throws Exception {
		get();
	}
	
	public void stop() throws Exception {
		if (isInstantiated()) {
			ServerBootstrapConfig config = get().config();
			
			config.group().shutdownGracefully();
			config.childGroup().shutdownGracefully();
		}
	}
}
