package com.appe.framework.json.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.inject.Singleton;

import com.appe.framework.json.Externalizer;
import com.appe.framework.util.DateFormats;
import com.appe.framework.util.Dictionary;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
	 * 
	 */
	public JacksonMapper() {
		//DEFAULT FEATURES
		configure();
		
		//CONFIGURE CUSTOM MODULE
		SimpleModule module = new SimpleModule("appe.json");
		configure(module);
		
		//REGISTER CUSTOM MODULE
		registerModule(module);
	}
	
	/**
	 * configure default feature
	 */
	protected void configure() {
		//ONLY FIELD
		setVisibility(PropertyAccessor.FIELD, 	 Visibility.ANY);
		setVisibility(PropertyAccessor.GETTER, 	 Visibility.NONE);
		setVisibility(PropertyAccessor.SETTER, 	 Visibility.NONE);
		setVisibility(PropertyAccessor.IS_GETTER,Visibility.NONE);
		setVisibility(PropertyAccessor.CREATOR,  Visibility.NONE);
		
		//WRITE EMPTY
		enable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
		
		//DISABLEs
		disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		//DEFAULT DATE FORMAT
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		setDateFormat(DateFormats.getUTC(DateFormats.ISO8601));
	}
	
	/**
	 * Register custom module for special enhancing of Dictionary.
	 * 
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "serial" , "unchecked"})
	protected void configure(SimpleModule module) {
		final UntypedObjectDeserializer deserializer = new UntypedObjectDeserializer() {
			@Override
			protected Object mapObject(JsonParser jp, DeserializationContext ctxt)
					throws IOException, JsonProcessingException {
				Object result = super.mapObject(jp, ctxt);
				//MAKE SURE TO WRAP THE CORRECT OBJECT
				if(result instanceof Map) {
					return new Dictionary((Map<String, Object>)result);
				}
				return result;
			}
		};
		
		//CUSTOME MODULE
		module.addDeserializer(Map.class, new JsonDeserializer<Dictionary>() {
			@Override
			public Dictionary deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				return	(Dictionary)deserializer.deserialize(jp, ctxt);
			}
		});
	}
	
	/**
	 * 
	 */
	@Override
	public String contentType() {
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
	public <T> T unmarshal(InputStream src, Class<T> type) throws IOException {
		return readValue(src, type);
	}
	
}
