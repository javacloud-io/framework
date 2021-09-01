package javacloud.framework.netty;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestApplication implements ServerApplication {

	@Override
	public List<ChannelHandler> serverInterceptors() {
		return Collections.singletonList(new LoggingHandler(LogLevel.INFO));
	}

	@Override
	public List<ChannelHandler> protocolHandlers() {
		return Collections.singletonList(new ChannelInboundHandlerAdapter() {

			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				System.out.println(ctx);
				ctx.writeAndFlush(Unpooled.copiedBuffer("Hello World!", Charset.defaultCharset()))
				   .addListener(new ChannelFutureListener() {
			            @Override
			            public void operationComplete(ChannelFuture future) {
			            	future.channel().close();
			            }
					});
			}
			
		});
	}
}
