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
package com.appe.ext.internal;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Make sure jackson using custom object mapper.
 * 
 * @author tobi
 *
 */
public class JacksonJaxbProvider extends com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider {
	/**
	 * Inject from registry
	 * 
	 * @param objectMapper
	 */
	@Inject
	public JacksonJaxbProvider(ObjectMapper objectMapper) {
		super(objectMapper, DEFAULT_ANNOTATIONS);
	}
}
