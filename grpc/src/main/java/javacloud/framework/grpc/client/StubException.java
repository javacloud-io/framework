package javacloud.framework.grpc.client;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import javacloud.framework.util.InternalException;

public class StubException extends RuntimeException implements InternalException.Reasonable {
	private static final long serialVersionUID = -6130633971701802078L;
	private final String reason;
	
	StubException(String reason, String message, StatusRuntimeException cause) {
		super(message, cause);
		this.reason = reason;
	}
	
	@Override
	public String getReason() {
		return reason;
	}
	
	@Override
	public StatusRuntimeException getCause() {
		return (StatusRuntimeException)super.getCause();
	}
	
	public Status getStatus() {
		return getCause().getStatus();
	}
	
	public Metadata getTrailers() {
		return getCause().getTrailers();
	}
}
