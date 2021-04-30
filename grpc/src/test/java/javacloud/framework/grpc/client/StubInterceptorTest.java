package javacloud.framework.grpc.client;

import org.junit.Test;

import io.grpc.Status;
import javacloud.framework.util.ValidationException;

public class StubInterceptorTest {
	@Test(expected = ValidationException.Conflict.class)
	public void testConflict() {
		new StubInterceptor<Void, Void>().invoke(t -> {
				throw Status.FAILED_PRECONDITION.asRuntimeException();
			}, null);
	}
	
	@Test(expected = ValidationException.AlreadyExists.class)
	public void testAlreadyExists() {
		new StubInterceptor<Void, Void>().invoke(t -> {
				throw Status.ALREADY_EXISTS.asRuntimeException();
			}, null);
	}
	
	@Test(expected = ValidationException.NotFound.class)
	public void testNotFound() {
		new StubInterceptor<Void, Void>().invoke(t -> {
				throw Status.NOT_FOUND.asRuntimeException();
			}, null);
	}
	
	@Test(expected = ValidationException.class)
	public void testInvalid() {
		new StubInterceptor<Void, Void>().invoke(t -> {
				throw Status.INVALID_ARGUMENT.asRuntimeException();
			}, null);
	}
	
	@Test(expected = RuntimeException.class)
	public void testGeneric() {
		new StubInterceptor<Void, Void>().invoke(t -> {
				throw Status.INTERNAL.asRuntimeException();
			}, null);
	}
}
