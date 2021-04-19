package javacloud.framework.grpc;

import javacloud.framework.config.ConfigProperty;

public interface StubSettings {
	@ConfigProperty(name="javacloud.grpc.stub.loadBalancingPolicy", value="round_robin")
	String loadBalancingPolicy();
	
	@ConfigProperty(name="javacloud.grpc.stub.retryEnabled", value="true")
	boolean retryEnabled();
	
	@ConfigProperty(name="javacloud.grpc.stub.timeoutMillis", value="1000")
	long timeoutMillis();
	
	@ConfigProperty(name="javacloud.grpc.stub.waitForReady", value="true")
	boolean waitForReady();
}
