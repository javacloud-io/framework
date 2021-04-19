package javacloud.framework.grpc.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.Server;
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
	
	protected void configure(NettyServerBuilder serverBuilder) {
		application.serverInterceptors()
			.forEach(i -> serverBuilder.intercept(i));
		application.serverServices()
			.forEach(s -> serverBuilder.addService(s));
	}
}
