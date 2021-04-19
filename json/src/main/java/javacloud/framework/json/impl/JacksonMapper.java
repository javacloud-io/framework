package javacloud.framework.json.impl;

import javacloud.framework.io.Externalizer;
import javacloud.framework.json.JsonValue;
import javacloud.framework.json.internal.JsonValues;
import javacloud.framework.util.DateFormats;
import javacloud.framework.util.Objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Baseline for JACKSON object mapping, make sure ALWAYS NICE AND POJO!
 * 
 * @author ho
 *
 */
@Singleton
public class JacksonMapper extends ObjectMapper implements Externalizer {
	private static final long serialVersionUID = -6439745503024511184L;
	
	/**
	 * DEFAULT FEATURES
	 */
	public JacksonMapper() {
		configure();
		
		//CONFIGURE CUSTOM MODULE
		SimpleModule module = new SimpleModule("javacloud.json");
		configure(module);
		
		//REGISTER CUSTOM MODULE
		registerModule(module);
	}
	
	/**
	 * configure default feature
	 */
	protected void configure() {
		//ONLY FIELDS
		setVisibility(PropertyAccessor.FIELD, 	 Visibility.ANY);
		setVisibility(PropertyAccessor.GETTER, 	 Visibility.NONE);
		setVisibility(PropertyAccessor.SETTER, 	 Visibility.NONE);
		setVisibility(PropertyAccessor.IS_GETTER,Visibility.NONE);
		setVisibility(PropertyAccessor.CREATOR,  Visibility.NONE);
		
		//DISABLEs
		//disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		//DEFAULT DATE FORMAT
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		setDateFormat(DateFormats.getUTC(DateFormats.ISO8601));
	}
	
	/**
	 * 
	 * @param module
	 */
	@SuppressWarnings("serial")
	protected void configure(SimpleModule module) {
		//JSON VALUE
		module.addSerializer(JsonValue.class, new StdSerializer<JsonValue>(JsonValue.class) {
			@Override
			public void serialize(JsonValue json, JsonGenerator gen, SerializerProvider provider) throws IOException {
				gen.writeObject(json.value());
			}
		});
		module.addDeserializer(JsonValue.class, new StdDeserializer<JsonValue>(JsonValue.class) {
			@Override
			public JsonValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				Object value = p.readValueAs(Object.class);
				return JsonValues.asValue(value);
			}
		});
	}
	
	/**
	 * return JSON as externalizer type
	 */
	@Override
	public String type() {
		return JSON;
	}
	
	/**
	 * 
	 */
	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		writeValue(dst, v);
	}
	
	/**
	 * 
	 */
	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		return Objects.cast(readValue(src, type));
	}
}
