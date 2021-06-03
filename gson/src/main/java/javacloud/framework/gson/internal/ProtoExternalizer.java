package javacloud.framework.gson.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Singleton;
import com.google.protobuf.Message;

import javacloud.framework.io.Externalizer;
import javacloud.framework.util.Objects;

@Singleton
public class ProtoExternalizer implements Externalizer {
	static final String PROTO = "proto";
	
	@Override
	public String type() {
		return PROTO;
	}

	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		Message message;
		if (v instanceof Message.Builder) {
			message = ((Message.Builder)v).build();
		} else {
			message = (Message)v;
		}
		dst.write(message.toByteArray());
	}
	
	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		Message.Builder builder = BuilderFactory.get().newBuilder(type);
		return Objects.cast(builder.mergeFrom(src).build());
	}
}
