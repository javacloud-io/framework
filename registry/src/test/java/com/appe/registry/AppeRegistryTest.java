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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import com.appe.registry.AppeRegistry;
import com.appe.registry.cdi.GuiceTestCase;
/**
 * 
 * @author ho
 *
 */
public class AppeRegistryTest extends GuiceTestCase {
	@Inject
	TestService testService;
	
	@Inject @Named("named")
	TestService namedService;
	@Test
	public void testInject() {
		Assert.assertNotNull(testService);
		Assert.assertNotNull(namedService);
	}
	
	/**
	 * 
	 */
	@Test
	public void testSingleton() {
		AppeConfig appec = AppeRegistry.get().getInstance(AppeConfig.class);
		Assert.assertSame(appec, AppeRegistry.get().getInstance(AppeConfig.class));
	}
	
	/**
	 * 
	 */
	@Test
	public void testInstance() {
		Assert.assertNotNull(AppeRegistry.get());
		
		TestService ts = AppeRegistry.get().getInstance(TestService.class);
		Assert.assertNotNull(ts);
		
		//ALWAYS NEW
		Assert.assertNotSame(ts, AppeRegistry.get().getInstance(TestService.class));
		Assert.assertNotNull(AppeRegistry.get().getInstance(TestInject.class).getService());
		Assert.assertNotNull(AppeRegistry.get().getInstance(TestInjectNamed.class).getService());
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
		Assert.assertEquals("Hello HO", i18nConfig.getLocalizedMessage("hello.1", "HO"));
	}
	
	/**
	 * 
	 */
	@Test
	public void testInstances() {
		List<TestService> services = AppeRegistry.get().getInstances(TestService.class);
		Assert.assertEquals(2, services.size());
	}
}
