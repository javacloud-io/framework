package io.javacloud.framework.json.impl;

import io.javacloud.framework.data.Externalizer;
import io.javacloud.framework.util.DateFormats;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
		disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		//DEFAULT DATE FORMAT
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		setDateFormat(DateFormats.getUTC(DateFormats.ISO8601));
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
	public <T> T unmarshal(InputStream src, Class<T> type) throws IOException {
		return readValue(src, type);
	}
}
