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
package com.appe.startup;

import com.appe.AppeNamespace;
import com.appe.util.JacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
/**
 * Automatically register & bind classes using basic services as backbone to build more complicated services.
 * 
 * @author ho
 *
 */
public class ServiceModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AppeNamespace.class).to(AppeNamespaceImpl.class);
		bind(ObjectMapper.class).to(JacksonMapper.class);
	}
}
