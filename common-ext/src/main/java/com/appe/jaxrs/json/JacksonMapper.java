/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.jaxrs.json;

import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;

import com.appe.util.DateFormats;
import com.appe.util.Dictionary;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class JacksonMapper extends ObjectMapper {
	private static final long serialVersionUID = -6439745503024511184L;
	/**
	 * 
	 */
	public JacksonMapper() {
		//DEFAULT FEATURES
		configure();
		
		//CONFIGURE CUSTOM MODULE
		SimpleModule module = new SimpleModule("APPE-json");
		configure(module);
		
		//REGISTER CUSTOM MODULE
		registerModule(module);
	}
	
	/**
	 * configure default feature
	 */
	protected void configure() {
		//ENABLEs
		enable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
		
		//DISABLEs
		disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		//DEFAULT DATE FORMAT
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		setDateFormat(DateFormats.get(DateFormats.DFL_ISO8601));
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
}
