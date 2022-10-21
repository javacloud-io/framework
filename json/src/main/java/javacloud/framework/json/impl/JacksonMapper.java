package javacloud.framework.json.impl;

import javacloud.framework.io.Externalizer;
import javacloud.framework.util.DateFormats;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
		configure(new JacksonSerde());
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
}
