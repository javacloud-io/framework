package io.javacloud.framework.config;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import io.javacloud.framework.cdi.ServiceTest;
import io.javacloud.framework.util.ResourceLoader;
/**
 * 
 * @author ho
 *
 */
public class ConfigRegistryTest extends ServiceTest {
	@Inject
	ConfigManager configRegistry;
	@Test
	public void testProperties() {
		SimpleConfig config = configRegistry.getConfig(SimpleConfig.class);
		Assert.assertNull(config.getAutoName());
		
		Assert.assertEquals("123", config.getNiceName());
		Assert.assertEquals(123, config.getIntValue());
		Assert.assertEquals("456", config.getRedefName());
		
		//test native implementation
		Assert.assertNotEquals(0, config.hashCode());
	}
	
	/**
	 * 
	 */
	@Test
	public void testLoader() {
		Assert.assertNotNull(ResourceLoader.loadService(ConfigManager.class));
	}
}
