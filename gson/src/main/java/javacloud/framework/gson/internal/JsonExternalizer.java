package javacloud.framework.gson.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import javacloud.framework.io.Externalizer;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Objects;

public class JsonExternalizer implements Externalizer {
	private final JsonFormat.Parser  parser;
	private final JsonFormat.Printer printer;
	
	public JsonExternalizer() {
		this.parser  = JsonFormat.parser();
		this.printer = JsonFormat.printer();
	}
	
	@Override
	public String type() {
		return JSON;
	}

	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		String json = toUTF8((MessageOrBuilder)v);
		dst.write(json.getBytes(Codecs.UTF8));
		dst.flush();
	}

	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		return Objects.cast(toMessage(new InputStreamReader(src), type));
	}
	
	public String toUTF8(MessageOrBuilder v) throws IOException {
		return	printer.print(v);
	}
	
	public <T extends Message> T toMessage(Reader src, Class<?> type) throws IOException {
		Message.Builder builder = BuilderFactory.get().newBuilder(type);
		parser.merge(src, builder);
		return Objects.cast(builder.build());
	}
}
