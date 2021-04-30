package javacloud.framework.grpc.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import javacloud.framework.grpc.internal.MetadataKeys;
import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.util.InternalException;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ValidationException;
import io.grpc.ServerCallHandler;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class ServerExceptionInterceptor implements ServerInterceptor {
	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
			ServerCallHandler<ReqT, RespT> next) {
		return new SimpleForwardingServerCallListener<ReqT>(next.startCall(call, headers)) {
			@Override
			public void onHalfClose() {
				try {
					super.onHalfClose();
				} catch (RuntimeException ex) {
					closeCall(call, ex);
				}
			}
		};
	}
	
	protected <ReqT, RespT> void closeCall(ServerCall<ReqT, RespT> call, Throwable cause) {
		Status status;
		Metadata trailers;
		if (cause instanceof StatusRuntimeException) {
			status = ((StatusRuntimeException)cause).getStatus();
			trailers = toTrailers(cause.getCause(), status.getDescription());
		} else {
			status = toStatus(cause).withCause(cause);
			trailers = toTrailers(cause, null);
		}
		
		// translate & localize
		String message = trailers.get(MetadataKeys.MESSAGE);
		if (!Objects.isEmpty(message)) {
			trailers.put(MetadataKeys.MESSAGE, toLocalizedMessage(message));
		}
		call.close(status.withDescription(message), trailers);
	}
	
	protected Status toStatus(Throwable cause) {
		//AUTHZ
		if (cause instanceof AuthenticationException) {
			if (cause instanceof AccessDeniedException) {
				return	Status.PERMISSION_DENIED;
			} else {
				return	Status.UNAUTHENTICATED;
			}
		}
		//CLONFLICT
		if (cause instanceof ValidationException.AlreadyExists) {
			return	Status.ALREADY_EXISTS;
		}
		if (cause instanceof ValidationException.Conflict) {
			return	Status.FAILED_PRECONDITION;
		}
		//NOT FOUND
		if (cause instanceof ValidationException.NotFound
				|| cause instanceof java.io.FileNotFoundException) {
			return	Status.NOT_FOUND;
		}
		//VALIDATION
		if (cause instanceof ValidationException
				|| cause instanceof IllegalArgumentException) {
			return	Status.INVALID_ARGUMENT;
		}
		return	Status.INTERNAL;
	}
	
	protected Metadata toTrailers(Throwable exception, String message) {
		//REASON ERROR
		String error = InternalException.getReason(exception);
		
		//DETAILS MESSAGE LOCALE
		if (Objects.isEmpty(message)) {
			message = exception.getMessage();
			
			// fall back to class name
			if (Objects.isEmpty(message)) {
				message = exception.getClass().getName();
			}
		}
		
		Metadata metadata = new Metadata();
		metadata.put(MetadataKeys.ERROR, error);
		metadata.put(MetadataKeys.MESSAGE, message);
		return metadata;
	}
	
	/**
	 * 
	 * @param message
	 * @return the localized message if any found
	 */
	protected String toLocalizedMessage(String message) {
		return	message;
	}
}
