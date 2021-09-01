package javacloud.framework.netty;

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javacloud.framework.cdi.internal.IntegrationTest;

public class ApplicationIT extends IntegrationTest {
	@Test
	public void testConnect() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        client.group(group)
         	  .channel(NioSocketChannel.class)
         	  .handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline()
					  .addLast(new ChannelInboundHandlerAdapter() {
						  @Override
						  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							  String text = ((ByteBuf)msg).toString(Charset.defaultCharset());
							  Assert.assertEquals("Hello World!", text);
						  }
					});
				}
			}).option(ChannelOption.AUTO_READ, true);
        
        try {
	        ChannelFuture f = client.connect("localhost", 8888).sync();
	        
	        // Wait until the connection is closed.
	        f.channel().closeFuture().sync();
        } finally {
        	group.shutdownGracefully();
        }
	}
}
