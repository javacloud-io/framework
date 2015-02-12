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
package com.appe.registry;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.appe.registry.AppeRegistry;
import com.appe.registry.spi.GuiceTestCase;
/**
 * 
 * @author ho
 *
 */
public class AppeRegistryTest extends GuiceTestCase {
	@Inject
	TestService testService;
	
	@Test
	public void testInject() {
		Assert.assertNotNull(testService);
	}
	
	/**
	 * 
	 */
	@Test
	public void testInstance() {
		Assert.assertNotNull(AppeRegistry.get());
		Assert.assertNotNull(AppeRegistry.get().getInstance(TestService.class));
	}
	
	/**
	 * 
	 */
	@Test
	public void testConfig() {
		TestConfig tconfig= AppeRegistry.get().getConfig(TestConfig.class);
		Assert.assertNotNull(tconfig);
		
		//LOAD CONFIG
		Assert.assertEquals(1, tconfig.numberOfCars());
		Assert.assertEquals("lalala", tconfig.message());
		Assert.assertNull(tconfig.notAvailable());
	}
	
	@Test
	public void testI18nConfig() {
		//LOAD I18N
		TestI18nConfig i18nConfig = AppeRegistry.get().getConfig(TestI18nConfig.class);
		Assert.assertEquals("Hello", i18nConfig.hello());
		Assert.assertEquals("Hello HO", i18nConfig.hello("HO"));
		
		//MISSING WILL RETURN NULL
		Assert.assertEquals("missing", i18nConfig.missing());
		
		//DYNAMIC
		Assert.assertEquals("Hello HO", i18nConfig.getMessage("hello.1", "HO"));
	}
}
