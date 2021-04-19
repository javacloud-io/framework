package javacloud.framework.grpc.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import javacloud.framework.grpc.internal.MetadataKeys;
import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.util.GenericException;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ValidationException;
import io.grpc.ServerCallHandler;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class GenericExceptionInterceptor implements ServerInterceptor {
	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
			ServerCallHandler<ReqT, RespT> next) {
		return new SimpleForwardingServerCallListener<ReqT>(next.startCall(call, headers)) {
			@Override
			public void onHalfClose() {
				try {
					super.onHalfClose();
				} catch (Throwable ex) {
					closeCall(call, ex);
				}
			}
		};
	}
	
	protected <ReqT, RespT> void closeCall(ServerCall<ReqT, RespT> call, Throwable exception) {
		Status status;
		Metadata trailers;
		if (exception instanceof StatusRuntimeException) {
			status = ((StatusRuntimeException)exception).getStatus();
			trailers = toTrailers(exception.getCause(), status.getDescription());
		} else {
			status = toStatus(exception).withCause(exception);
			trailers = toTrailers(exception, null);
		}
		
		// translate & localize
		String message = trailers.get(MetadataKeys.MESSAGE);
		if (!Objects.isEmpty(message)) {
			trailers.put(MetadataKeys.MESSAGE, toLocalizedMessage(message));
		}
		call.close(status.withDescription(message), trailers);
	}
	
	protected Status toStatus(Throwable exception) {
		//AUTHZ
		if(exception instanceof AuthenticationException) {
			if(exception instanceof AccessDeniedException) {
				return	Status.PERMISSION_DENIED;
			} else {
				return	Status.UNAUTHENTICATED;
			}
		}
		//CLONFLICT
		if(exception instanceof ValidationException.Conflict) {
			return	Status.ALREADY_EXISTS;
		}
		//NOT FOUND
		if(exception instanceof ValidationException.NotFound
				|| exception instanceof java.io.FileNotFoundException) {
			return	Status.NOT_FOUND;
		}
		//VALIDATION
		if(exception instanceof ValidationException
				|| exception instanceof IllegalArgumentException) {
			return	Status.INVALID_ARGUMENT;
		}
		return	Status.INTERNAL;
	}
	
	protected Metadata toTrailers(Throwable exception, String message) {
		//REASON ERROR
		String error = GenericException.getReason(exception);
		
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
