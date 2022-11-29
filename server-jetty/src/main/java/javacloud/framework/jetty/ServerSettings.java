package javacloud.framework.jetty;

import javacloud.framework.config.ConfigProperty;

public interface ServerSettings {
	@ConfigProperty(name = "javacloud.jetty.server.address", value = "localhost")
	String serverAddress();
	
	@ConfigProperty(name = "javacloud.jetty.server.port", value = "8081")
	int serverPort();
	
	@ConfigProperty(name = "javacloud.jetty.server.contextPath", value = "/")
	String contextPath();
}
