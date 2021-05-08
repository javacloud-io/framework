package javacloud.framework.gson.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.protobuf.Message;

import javacloud.framework.io.Externalizer;
import javacloud.framework.util.Objects;

public class ProtoExternalizer implements Externalizer {
	static final String PROTO = "PROTO";
	
	@Override
	public String type() {
		return PROTO;
	}

	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		Message message = Objects.cast(v);
		dst.write(message.toByteArray());
	}
	
	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		Message.Builder builder = BuilderFactory.get().newBuilder(type);
		return Objects.cast(builder.mergeFrom(src).build());
	}
}
