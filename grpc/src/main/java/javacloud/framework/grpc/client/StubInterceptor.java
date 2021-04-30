package javacloud.framework.grpc.client;

import java.util.Optional;
import java.util.function.Function;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import javacloud.framework.grpc.internal.MetadataKeys;
import javacloud.framework.util.ValidationException;

public class StubInterceptor<T, R> implements Function<StatusRuntimeException, RuntimeException> {
	
	public R invoke(Function<T, R> call, T args) {
		try {
			return call.apply(args);
		} catch (StatusRuntimeException ex) {
			throw apply(ex);
		}
	}
	
	@Override
	public RuntimeException apply(StatusRuntimeException cause) {
		Optional<Metadata> trailers = Optional.ofNullable(cause.getTrailers());
		Status status = cause.getStatus();
		
		String error = trailers.map(metadata -> metadata.get(MetadataKeys.ERROR))
							   .orElse(status.getCode().name());
		String message = trailers.map(metadata -> metadata.get(MetadataKeys.MESSAGE))
								 .orElseGet(status::getDescription);
		
		Status.Code code = status.getCode();
		if (code == Status.Code.NOT_FOUND) {
			return new ValidationException.NotFound(error, message);
		}
		
		if (code == Status.Code.ALREADY_EXISTS) {
			return new ValidationException.AlreadyExists(error, message);
		}
		
		if (code == Status.Code.FAILED_PRECONDITION) {
			return new ValidationException.Conflict(error, message);
		}
		
		if (code == Status.Code.INVALID_ARGUMENT) {
			return new ValidationException(error, message);
		}
		
		return new StubException(error, message, cause);
	}
}
