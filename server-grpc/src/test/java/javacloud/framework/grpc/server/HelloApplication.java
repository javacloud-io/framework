package javacloud.framework.grpc.server;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import javacloud.framework.grpc.ServerApplication;

@Singleton
public class HelloApplication implements ServerApplication {

	@Override
	public List<ServerServiceDefinition> serverServices() {
		return Collections.singletonList(ServerInterceptors.intercept(new GreeterImpl()));
	}
	
	@Override
	public List<ServerInterceptor> serverInterceptors() {
		return Collections.singletonList(new GenericExceptionInterceptor());
	}
	
	static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
	    @Override
	    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
	    	if (Objects.equals("bad", req.getName())) {
	    		throw new IllegalArgumentException("bad name");
	    	}
	    	
	    	HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
	    	responseObserver.onNext(reply);
	    	responseObserver.onCompleted();
	    }
	}
}
