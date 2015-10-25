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
package com.appe.data;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.appe.registry.internal.GuiceTestCase;
import com.appe.security.Identifiable;
import com.appe.util.Dictionary;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author ho
 *
 */
public class JacksonMapperTest extends GuiceTestCase {
	@Inject
	ObjectMapper mapper;
	
	@Test
	public void testMapper() throws JsonParseException, JsonMappingException, IOException {
		Dictionary dict = mapper.readValue("{\"a\":1, \"d\": {\"c\":true}}", Dictionary.class);
		Assert.assertEquals((Integer)1, dict.getInteger("a"));
		
		//make sure map always dictionary type
		Assert.assertEquals(Dictionary.class, dict.get("d").getClass());
	}
	
	@Test
	public void testNull() throws Exception {
		String value = mapper.writeValueAsString(new Identifiable<String>(){});
		Assert.assertEquals("{}", value);
	}
}
