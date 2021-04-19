package javacloud.framework.grpc.stub;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
/**
 * 
 * Obtains managed channel from host/port or in-process:
 * 
 * mcb = NettyChannelBuilder.forAddress(host, port)
 * mcb = ManagedChannelBuilder.forAddress(host, port)
 * mcb = InProcessChannelBuilder.forName(name)
 * 
 * @author tobiho
 *
 * @param <S>
 * @param <SP>
 */
public abstract class StubBuilder <S extends AbstractStub<S>, SP extends StubSupplier<S>> {
	private final ManagedChannelBuilder<?> mcb;
	private final Map<String, Object> config = new HashMap<>();
	
	public StubBuilder(String serviceName, String address, int port) {
		this(serviceName, ManagedChannelBuilder.forAddress(address, port));
	}
	
	public StubBuilder(String serviceName, ManagedChannelBuilder<?> mcb) {
		this.mcb = mcb;
		config.put("name", Collections.singletonMap("service", serviceName));
	}
	
	public StubBuilder<S, SP> withLoadBalancingPolicy(String loadBalancingPolicy) {
		mcb.defaultLoadBalancingPolicy(loadBalancingPolicy);
		return this;
	}
	
	public StubBuilder<S, SP> withDefaultLoadBalancing() {
		try {
			withLoadBalancingPolicy("round_robin");
		} catch (RuntimeException ex) {}
		return this;
	}
	
	// Interceptors run in the reverse order in which they are added
	public StubBuilder<S, SP> withInterceptors(List<ClientInterceptor> interceptors) {
		mcb.intercept(interceptors);
		return this;
	}
	
	public StubBuilder<S, SP> withRetryEnabled(boolean retryEnabled) {
		if (retryEnabled) {
			mcb.enableRetry();
		} else {
			mcb.disableRetry();
		}
		return this;
	}
	
	public StubBuilder<S, SP> withWaitForReady(boolean waitForReady) {
		config.put("waitForReady", Boolean.valueOf(waitForReady));
		return this;
	}
	
	public StubBuilder<S, SP> withTimeoutMillis(long timeoutMillis) {
		long seconds = TimeUnit.MILLISECONDS.toSeconds(timeoutMillis);
		long nanos	 = TimeUnit.MILLISECONDS.toNanos(timeoutMillis - TimeUnit.SECONDS.toMillis(seconds));
		//seconds.nanos
		String duration;
		if (nanos == 0) {
			duration = String.format("%ds", seconds);
		} else {
			duration = String.format("%d.%09ds",seconds, nanos);
		}
		config.put("timeout", duration);
		return this;
	}
	
	/**
	 * @see https://github.com/grpc/grpc/blob/master/doc/service_config.md
	 * 
	 * @return
	 */
	public SP build() {
		mcb.defaultServiceConfig(Collections.singletonMap("methodConfig", Collections.singletonList(config)));
		return newStub(mcb.build());
	}
	
	/**
	 * Simply construct new StubSupplier<>(XxxGrpc.newXXXStub(channel))
	 * 
	 * @param channel
	 * @return StubSupplier
	 */
	protected abstract SP newStub(ManagedChannel channel);
}
