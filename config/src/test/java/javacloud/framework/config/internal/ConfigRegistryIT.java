package javacloud.framework.config.internal;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.cdi.internal.IntegrationTest;
import javacloud.framework.config.ConfigManager;
/**
 * 
 * @author ho
 *
 */
public class ConfigRegistryIT extends IntegrationTest {
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
}
