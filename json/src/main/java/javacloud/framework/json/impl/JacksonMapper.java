package javacloud.framework.json.impl;

import javacloud.framework.io.Externalizer;
import javacloud.framework.json.JsonValue;
import javacloud.framework.json.internal.JsonObject;
import javacloud.framework.util.DateFormats;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

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
	private static final Logger logger = Logger.getLogger(JacksonMapper.class.getName());
	
	/**
	 * DEFAULT FEATURES
	 */
	public JacksonMapper() {
		//ONLY FIELDS
		setVisibility(PropertyAccessor.FIELD, 	 Visibility.ANY);
		setVisibility(PropertyAccessor.GETTER, 	 Visibility.NONE);
		setVisibility(PropertyAccessor.SETTER, 	 Visibility.NONE);
		setVisibility(PropertyAccessor.IS_GETTER,Visibility.NONE);
		setVisibility(PropertyAccessor.CREATOR,  Visibility.NONE);
		
		//ENABLEs
		//enable(SerializationFeature.INDENT_OUTPUT);
		
		//DISABLEs
		//disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		//DEFAULT DATE FORMAT
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		setDateFormat(DateFormats.getUTC(DateFormats.ISO8601_S3));
		
		// register default module
		configure(new SimpleSerde());
	}
	
	/**
	 * return JSON as externalizer type
	 */
	@Override
	public String type() {
		return JSON;
	}
	
	/**
	 * Allows simple extension module
	 * 
	 * @param module
	 */
	protected void configure(SimpleModule module ) {
		registerModule(module);
		
		// LOAD and register custom JACKSON SERDES, classpath META-INF/services/com.fasterxml.jackson.databind.Module
		for(com.fasterxml.jackson.databind.Module serde: ResourceLoader.loadServices(com.fasterxml.jackson.databind.Module.class)) {
			logger.fine("Register custom jackson module " + serde.getClass().getName());
			registerModule(serde);
		}
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
	
	
	static class SimpleSerde extends SimpleModule {
		private static final long serialVersionUID = 1204273612914943898L;
		
		@SuppressWarnings("serial")
		public SimpleSerde() {
			super("javacloud.json");
			// deserialize date
			addDeserializer(Date.class, new StdDeserializer<Date>(Date.class) {
				@Override
				public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
					String sdate = p.getText();
					try {
						return parseDate(sdate);
					} catch (ParseException ex) {
						throw new JsonProcessingException("Not support date format " + sdate, ex) {};
					}
				}
			});
			
			//JSON VALUE
			addSerializer(JsonValue.class, new StdSerializer<JsonValue>(JsonValue.class) {
				@Override
				public void serialize(JsonValue json, JsonGenerator gen, SerializerProvider provider) throws IOException {
					gen.writeObject(json.value());
				}
			});
			
			addDeserializer(JsonValue.class, new StdDeserializer<JsonValue>(JsonValue.class) {
				@Override
				public JsonValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
					Object value = p.readValueAs(Object.class);
					return JsonObject.of(value);
				}
			});
		}
		
		/**
		 * 
		 * @param sdate
		 * @return
		 * @throws ParseException
		 */
		static Date parseDate(String sdate) throws ParseException {
			if (sdate.endsWith("Z") && sdate.length() >= 10) {
				if (sdate.charAt(sdate.length() - 5) == '.') {
					return DateFormats.getUTC(DateFormats.ISO8601_S3).parse(sdate);
				}
				return DateFormats.getUTC(DateFormats.ISO8601).parse(sdate);
			}
			return DateFormats.get(DateFormats.LOCAL, TimeZone.getDefault()).parse(sdate);
		}
	}
}
