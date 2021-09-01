package javacloud.framework.netty.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javacloud.framework.config.ConfigManager;
import javacloud.framework.netty.ServerApplication;
import javacloud.framework.netty.ServerLifecycle;
import javacloud.framework.netty.ServerSettings;
import javacloud.framework.util.InternalException;


@Singleton
public class NettyServerLifecycleImpl extends ServerLifecycle {
	private final ServerApplication application;
	private final ServerSettings settings;
	private ChannelFuture awaitFuture;
	
	@Inject
	public NettyServerLifecycleImpl(ServerApplication application, ConfigManager configManager) {
		this(application, configManager.getConfig(ServerSettings.class));
	}
	
	protected NettyServerLifecycleImpl(ServerApplication application, ServerSettings settings) {
		this.application = application;
		this.settings = settings;
	}
	
	@Override
	protected ServerBootstrap newInstance() {
		// 1 dedicated hand off thread
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        
        // always new thread
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
    	// basic configuration
		ServerBootstrap server = new ServerBootstrap();
		server.group(bossGroup, workerGroup)
			  .channel(NioServerSocketChannel.class)
			  .childOption(ChannelOption.AUTO_READ, settings.autoRead());
		
		// SSL/TLS
		
		// extends configuration
		configure(server);
		return server;
	}
	
	@Override
	public void start() throws Exception {
		ServerBootstrap server = get();
		try {
			SocketAddress socketAddress = new InetSocketAddress(
					settings.serverAddress(),
					settings.serverPort());
			
			// Bind and start to accept incoming connections.
			this.awaitFuture = server.bind(socketAddress).sync()
									 .channel().closeFuture();
		} catch (Exception ex) {
        	throw InternalException.of(ex);
        }
	}
	
	// Wait until the server socket is closed.
	public void awaitTermination() throws Exception {
		this.awaitFuture.sync();
	}
	
	protected void configure(ServerBootstrap server) {
		// shared handlers
		final List<ChannelHandler> handlers = application.serverInterceptors();
		if (handlers.size() == 1) {
			server.handler(handlers.get(0));
		} else {
			server.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					handlers.forEach(h -> p.addLast(h));
				}
			});
		}
		
		// protocol handlers
		server.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();
				application.protocolHandlers().forEach(h -> p.addLast(h));
			}
		});
	}
}
