package com.appe.framework;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import com.appe.framework.AppeRegistry;
import com.appe.framework.internal.GuiceTestCase;
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
	public void testInstances() {
		List<TestService> services = AppeRegistry.get().getInstances(TestService.class);
		Assert.assertEquals(2, services.size());
	}
}
