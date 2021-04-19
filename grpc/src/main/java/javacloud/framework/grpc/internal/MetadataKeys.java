package javacloud.framework.grpc.internal;

import io.grpc.Metadata;

public interface MetadataKeys {
	Metadata.Key<String> ERROR 		= Metadata.Key.of("error", Metadata.ASCII_STRING_MARSHALLER);
	Metadata.Key<String> MESSAGE	= Metadata.Key.of("message", Metadata.ASCII_STRING_MARSHALLER);
}
