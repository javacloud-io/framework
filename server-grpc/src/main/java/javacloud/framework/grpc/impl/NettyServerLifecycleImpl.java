package javacloud.framework.grpc.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.Server;
import io.grpc.ServerInterceptor;
import io.grpc.netty.NettyServerBuilder;
import javacloud.framework.config.ConfigManager;
import javacloud.framework.grpc.ServerSettings;
import javacloud.framework.grpc.ServerApplication;
import javacloud.framework.grpc.ServerLifecycle;

@Singleton
public class NettyServerLifecycleImpl extends ServerLifecycle {
	private final ServerApplication application;
	private final ServerSettings settings;
	
	@Inject
	public NettyServerLifecycleImpl(ServerApplication application, ConfigManager configManager) {
		this(application, configManager.getConfig(ServerSettings.class));
	}
	
	protected NettyServerLifecycleImpl(ServerApplication application, ServerSettings settings) {
		this.application = application;
		this.settings = settings;
	}
	
	@Override
	protected Server newInstance() {
		SocketAddress socketAddress = new InetSocketAddress(
				settings.serverAddress(),
				settings.serverPort());
		
		// basic configuration
		NettyServerBuilder serverBuilder = NettyServerBuilder.forAddress(socketAddress)
				.maxConcurrentCallsPerConnection(settings.maxConcurrentCallsPerConnection())
				.maxConnectionAge(settings.maxConnectionAgeMillis(), TimeUnit.MILLISECONDS)
				.maxConnectionIdle(settings.maxConnectionIdleMillis(), TimeUnit.MILLISECONDS);
		// SSL/TLS
		
		// extends configuration
		configure(serverBuilder);
		return serverBuilder.build();
	}
	
	public void awaitTermination() throws Exception {
		Server server = get();
		server.awaitTermination();
	}
	
	protected void configure(NettyServerBuilder serverBuilder) {
		// reverse the interceptors to ensure FIFO
		List<ServerInterceptor> interceptors = application.serverInterceptors();
		for (int i = interceptors.size() - 1; i >= 0; i --) {
			serverBuilder.intercept(interceptors.get(i));
		}
		
		// add all services
		application.serverServices()
			.forEach(s -> serverBuilder.addService(s));
	}
}
