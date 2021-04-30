package javacloud.framework.grpc.server;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloRequest;
import javacloud.framework.cdi.junit.IntegrationTest;
import javacloud.framework.config.ConfigManager;
import javacloud.framework.grpc.ServerSettings;
import javacloud.framework.grpc.client.StubInterceptor;
import javacloud.framework.grpc.client.StubSupplier;
import javacloud.framework.util.ValidationException;

/**
 * GRPC server will automatically started as part of runlist
 *
 */
public class HelloIT extends IntegrationTest {
	@Inject
	ConfigManager configManager;
	
	@Test
	public void testHello() {
		ServerSettings settings = configManager.getConfig(ServerSettings.class);
		StubSupplier<GreeterGrpc.GreeterBlockingStub> stub = new StubSupplier<GreeterGrpc.GreeterBlockingStub>(
				GreeterGrpc.newBlockingStub(
						ManagedChannelBuilder.forAddress(settings.serverAddress(), settings.serverPort())
						.usePlaintext()
						.build()));
		stub.get().sayHello(HelloRequest.newBuilder().setName("hello").build());
	}
	
	@Test
	public void testBad() {
		ServerSettings settings = configManager.getConfig(ServerSettings.class);
		StubSupplier<GreeterGrpc.GreeterBlockingStub> stub = new StubSupplier<GreeterGrpc.GreeterBlockingStub>(
				GreeterGrpc.newBlockingStub(
						ManagedChannelBuilder.forAddress(settings.serverAddress(), settings.serverPort())
						.usePlaintext()
						.build()));
		try {
			stub.get().sayHello(HelloRequest.newBuilder().setName("bad").build());
			Assert.fail("expecting bad request");
		} catch(StatusRuntimeException ex) {
			Assert.assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());
		}
	}
	
	@Test(expected = ValidationException.class)
	public void testClientExceptionInterceptor() {
		ServerSettings settings = configManager.getConfig(ServerSettings.class);
		StubSupplier<GreeterGrpc.GreeterBlockingStub> stub = new StubSupplier<GreeterGrpc.GreeterBlockingStub>(
				GreeterGrpc.newBlockingStub(
						ManagedChannelBuilder.forAddress(settings.serverAddress(), settings.serverPort())
						.usePlaintext()
						.build()));
		
		// invoke with wrapper
		new StubInterceptor<>().invoke(
				t -> stub.get().sayHello(HelloRequest.newBuilder().setName("bad").build()),
				null);
	}
}
