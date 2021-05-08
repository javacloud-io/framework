package javacloud.framework.gson.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
	}
	
	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		if (v instanceof MessageOrBuilder) {
			super.marshal(v, dst);
		} else {
			if (v instanceof JsonValue) {
				v = ((JsonValue)v).value();
			}
			
			OutputStreamWriter writer = new OutputStreamWriter(dst);
			gson.toJson((Object)v, writer);
			writer.flush();
		}
	}

	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		if (Message.class.isAssignableFrom(type)) {
			return super.unmarshal(src, type);
		} else if (JsonValue.class.isAssignableFrom(type)) {
			return Objects.cast(JsonObject.of(gson.fromJson(new InputStreamReader(src), Object.class)));
		}
		return Objects.cast(gson.fromJson(new InputStreamReader(src), type));
	}
	
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
