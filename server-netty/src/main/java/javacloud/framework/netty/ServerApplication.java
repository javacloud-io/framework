package javacloud.framework.netty;

import java.util.Collections;
import java.util.List;

import io.netty.channel.ChannelHandler;

public interface ServerApplication {
	/**
	 * 
	 * @return shared server handlers
	 */
	default List<ChannelHandler> serverInterceptors() {
		return Collections.emptyList();
	}
	
	/**
	 * 
	 * @return protocol handlers
	 */
	List<ChannelHandler> protocolHandlers();
}
