package io.javacloud.framework.config;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import io.javacloud.framework.cdi.ServiceTest;
/**
 * 
 * @author ho
 *
 */
public class ConfigFactoryTest extends ServiceTest {
	@Inject
	ConfigFactory configFactory;
	@Test
	public void testProperties() {
		SimpleConfig config = configFactory.getConfig(SimpleConfig.class);
		Assert.assertNull(config.getAutoName());
		
		Assert.assertEquals("123", config.getNiceName());
		Assert.assertEquals(123, config.getIntValue());
		Assert.assertEquals("456", config.getRedefName());
		
		//test native implementation
		Assert.assertNotEquals(0, config.hashCode());
	}
}
