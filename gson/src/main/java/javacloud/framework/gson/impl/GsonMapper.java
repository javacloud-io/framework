package javacloud.framework.gson.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

import javacloud.framework.gson.internal.JsonExternalizer;
import javacloud.framework.json.JsonValue;
import javacloud.framework.json.internal.JsonObject;
import javacloud.framework.util.DateFormats;
import javacloud.framework.util.Objects;

@Singleton
public class GsonMapper extends JsonExternalizer {
	private final Gson gson;
	
	public GsonMapper() {
		super();
		
		// custom builder
		GsonBuilder builder =  new GsonBuilder();
		configure(builder);
		this.gson = builder.create();
	}
	
	protected void configure(GsonBuilder builder) {
		builder.registerTypeAdapter(Date.class, new UTCDateAdapter());
		builder.registerTypeHierarchyAdapter(JsonValue.class, new JsonValueAdapter());
		
		// dynamic adapters
		builder.registerTypeAdapterFactory(new TypeAdapterFactory() {
			@Override
			public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
				Class<?> rawType = type.getRawType();
				if (MessageOrBuilder.class.isAssignableFrom(rawType)) {
					return Objects.cast(new ProtoAdapter(rawType));
				}
				// NOT APPLICABLE
				return null;
			}
		});
	}
	
	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		if (v instanceof MessageOrBuilder) {
			super.marshal(v, dst);
		} else {
			OutputStreamWriter writer = new OutputStreamWriter(dst);
			gson.toJson(v, writer);
			writer.flush();
		}
	}

	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		if (Message.class.isAssignableFrom(type)) {
			return super.unmarshal(src, type);
		}
		return Objects.cast(gson.fromJson(new InputStreamReader(src), type));
	}
	
	// GSON will cache the adapter
	class JsonValueAdapter implements JsonSerializer<JsonValue>, JsonDeserializer<JsonValue> {
		@Override
		public JsonValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return JsonObject.of(gson.fromJson(json, Object.class));
		}

		@Override
		public JsonElement serialize(JsonValue src, Type typeOfSrc, JsonSerializationContext context) {
			Object value = src.value();
			return gson.toJsonTree(value);
		}
	}
	
	// FIXME: slow on READ
	class ProtoAdapter extends TypeAdapter<MessageOrBuilder> {
		final Class<?> type;
		public ProtoAdapter(Class<?> type) {
			this.type = type;
		}
		
		@Override
		public void write(JsonWriter out, MessageOrBuilder src) throws IOException {
			out.jsonValue(toUTF8(src));
		}

		@Override
		public MessageOrBuilder read(JsonReader in) throws IOException {
			JsonElement json = JsonParser.parseReader(in);
			return toMessage(new StringReader(json.toString()), type);
		}
	}
	
	// UTC
	static class UTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
		private final DateFormat dateFormat = DateFormats.getUTC(DateFormats.ISO8601);

		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			synchronized (dateFormat) {
				return new JsonPrimitive(dateFormat.format(src));
			}
		}
		
		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			try {
				synchronized (dateFormat) {
					return dateFormat.parse(json.getAsString());
				}
			} catch (ParseException ex) {
				throw new JsonParseException(ex);
			}
		}
	}
}
