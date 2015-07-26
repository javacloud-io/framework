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
package com.appe.server.internal;

import com.appe.registry.internal.GuiceFactory;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Make sure to bind SERVLET with same context listener so FILTER & JPA WORKING
 * 
 * @author ho
 *
 */
public class GuiceServletListener extends GuiceServletContextListener {
	@Override
	protected Injector getInjector() {
		return GuiceFactory.registryInjector();
	}
}
