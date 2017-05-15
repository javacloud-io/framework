package com.appe.framework.resource;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.appe.framework.internal.InjectionTest;
/**
 * 
 * @author ho
 *
 */
public class ResourceManagerTest extends InjectionTest {
	@Inject
	ResourceManager resourceManager;
	
	@Test
	public void testConfig() {
		TestConfig testConfig = resourceManager.getConfigBundle(TestConfig.class);
		Assert.assertEquals("xyz", testConfig.name());
		Assert.assertEquals("lala", testConfig.dummy());
	}
	
	@Test
	public void testMessages() {
		TestMessageBundle messageBundle = resourceManager.getMessageBundle(TestMessageBundle.class);
		Assert.assertEquals("pigpig", messageBundle.getMessage("pig"));
	}
	
	@Test
	public void testBundles() {
		MessageBundle messageBundle = resourceManager.getMessageBundle(MessageBundle.class);
		Assert.assertEquals("pigpig", messageBundle.getMessage("pig"));
	}
}
