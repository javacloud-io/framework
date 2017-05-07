package com.appe.framework.resource;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.appe.framework.internal.GuiceTestCase;
/**
 * 
 * @author ho
 *
 */
public class ResourceManagerTest extends GuiceTestCase {
	@Inject
	ResourceManager resourceManager;
	
	@Test
	public void testConfig() {
		TestConfig testConfig = resourceManager.getConfigBundle(TestConfig.class);
		Assert.assertEquals(testConfig.name(), "xyz");
	}
}
