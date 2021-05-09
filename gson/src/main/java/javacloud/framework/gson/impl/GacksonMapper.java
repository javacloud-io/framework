package javacloud.framework.gson.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

import javacloud.framework.gson.internal.JsonExternalizer;
import javacloud.framework.json.impl.JacksonMapper;

@Singleton
public class GacksonMapper extends JacksonMapper {
	private static final long serialVersionUID = 4813607671763278097L;
	
	private final Map<Class<?>, JsonDeserializer<?>> protoDeserializers = new ConcurrentHashMap<>();
	private final JsonExternalizer externalizer = new JsonExternalizer();
	
	public GacksonMapper() {
		super();
	}
	
	@Override
	@SuppressWarnings("serial")
	protected void configure(SimpleModule module) {
		//PROTO VALUE
		module.addSerializer(MessageOrBuilder.class, new StdSerializer<MessageOrBuilder>(MessageOrBuilder.class) {
			@Override
			public void serialize(MessageOrBuilder json, JsonGenerator gen, SerializerProvider provider) throws IOException {
				gen.writeRawValue(externalizer.toUTF8(json));
			}
		});
		
		module.setDeserializers(new SimpleDeserializers() {
			@Override
			public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
					BeanDescription beanDesc) throws JsonMappingException {
				if (type.isTypeOrSubTypeOf(Message.class)) {
					return protoDeserializer(type.getRawClass());
				}
				return super.findBeanDeserializer(type, config, beanDesc);
			}
		});
		super.configure(module);
	}
	
	@SuppressWarnings("serial")
	JsonDeserializer<?> protoDeserializer(Class<?> type) {
		JsonDeserializer<?> deserializer = protoDeserializers.get(type);
		if (deserializer == null) {
			deserializer = new StdDeserializer<Message>(type) {
				@Override
				public Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
					// FIXME: slow on READ
					String json = p.readValueAsTree().toString();
					return externalizer.toMessage(new StringReader(json), handledType());
				}
			};
			protoDeserializers.putIfAbsent(type, deserializer);
		}
		return deserializer;
	}
	
	@Override
	public void writeValue(OutputStream out, Object value)
			throws IOException, JsonGenerationException, JsonMappingException {
		if (value instanceof MessageOrBuilder) {
			externalizer.marshal(value, out);
		} else {
			super.writeValue(out, value);
		}
	}

	@Override
	public <T> T readValue(InputStream src, Class<T> valueType)
			throws IOException, JsonParseException, JsonMappingException {
		if (Message.class.isAssignableFrom(valueType)) {
			return externalizer.unmarshal(src, valueType);
		}
		return super.readValue(src, valueType);
	}
}
