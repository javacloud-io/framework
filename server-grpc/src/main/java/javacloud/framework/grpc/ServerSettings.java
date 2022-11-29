package javacloud.framework.grpc;

import javacloud.framework.config.ConfigProperty;

public interface ServerSettings {
	@ConfigProperty(name = "javacloud.grpc.server.address", value = "localhost")
	String serverAddress();
	
	@ConfigProperty(name = "javacloud.grpc.server.port", value = "8090")
	int serverPort();
	
	@ConfigProperty(name = "javacloud.grpc.server.maxConcurrentCallsPerConnection", value = "1000000")
	int maxConcurrentCallsPerConnection();
	
	@ConfigProperty(name = "javacloud.grpc.server.maxConnectionAgeMillis", value = "86400000")
	long maxConnectionAgeMillis();
	
	@ConfigProperty(name = "javacloud.grpc.server.maxConnectionIdleMillis", value = "86400000")
	long maxConnectionIdleMillis();
}
