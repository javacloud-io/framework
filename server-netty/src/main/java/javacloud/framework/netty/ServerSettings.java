package javacloud.framework.netty;

import javacloud.framework.config.ConfigProperty;

public interface ServerSettings {
	@ConfigProperty(name = "javacloud.netty.server.address", value = "0.0.0.0")
	String serverAddress();
	
	@ConfigProperty(name = "javacloud.netty.server.port", value = "8888")
	int serverPort();
	
	@ConfigProperty(name = "javacloud.netty.server.autoRead", value = "false")
	boolean autoRead();
}
