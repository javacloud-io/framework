package javacloud.framework.grpc.client;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;
/**
 * 
 * @author tobiho
 *
 * @param <S>
 */
public class StubSupplier<S extends AbstractStub<S>> implements Supplier<S>, AutoCloseable {
	private final S stub;
	
	public StubSupplier(S stub) {
		this.stub = stub;
	}

	@Override
	public S get() {
		return stub;
	}
	
	@Override
	public void close() throws Exception {
		Channel channel = stub.getChannel();
		if (channel instanceof ManagedChannel) {
			ManagedChannel managedChannel = (ManagedChannel)channel;
			
			if (!managedChannel.isShutdown()) {
				managedChannel.shutdown();
				awaitTermination(managedChannel);
			}
		}
	}
	
	protected void awaitTermination(ManagedChannel managedChannel)
			throws InterruptedException {
		while (!managedChannel.isTerminated()) {
			managedChannel.awaitTermination(5, TimeUnit.SECONDS);
		}
	}
}
