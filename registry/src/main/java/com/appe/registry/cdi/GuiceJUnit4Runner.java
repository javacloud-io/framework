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
package com.appe.registry.cdi;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Injector;
/**
 * Use to run anything test related to Guice. Using RunWith()
 * 
 * @author ho
 *
 */
public class GuiceJUnit4Runner extends BlockJUnit4ClassRunner {
	private Injector injector;
	/**
	 * 
	 * @param klass
	 * @throws InitializationError
	 */
	public GuiceJUnit4Runner(Class<?> klass) throws InitializationError {
		super(klass);
		this.injector = resolveInjector();
	}
	
	/**
	 * By default we always use the injector from registry
	 * 
	 * @return
	 */
	protected Injector resolveInjector() {
		return GuiceFactory.registryInjector();
	}
	
	/**
	 * Always inject the members to test instance
	 */
	@Override
	protected Object createTest() throws Exception {
		Object instance = super.createTest();
		injector.injectMembers(instance);
		return instance;
	}
	
}
