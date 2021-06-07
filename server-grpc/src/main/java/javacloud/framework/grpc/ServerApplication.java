package javacloud.framework.grpc;

import java.util.Collections;
import java.util.List;

import io.grpc.ServerInterceptor;
import io.grpc.ServerServiceDefinition;

public interface ServerApplication {	
	/**
	 * Interceptors run in the reverse order in which they are added
	 * 
	 * @return global interceptors for all services
	 */
	default List<ServerInterceptor> serverInterceptors() {
		return Collections.emptyList();
	}
	
	/**
	 * Using ServerInterceptors.intercept(...)
	 * 
	 * @return service definitions
	 */
	List<ServerServiceDefinition> serverServices();
}
