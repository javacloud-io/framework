package javacloud.framework.config.internal;

import java.util.Properties;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.cdi.test.ServiceTest;
import javacloud.framework.config.ConfigManager;
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
	public void testCliArgs() {
		Properties props = StandardConfigSource.parseProperties(new String[]{"-X", "-Y", "--xyz", "abc", "1", "2"});
		Assert.assertEquals("", props.get("X"));
		Assert.assertEquals("abc", props.get("xyz"));
	}
}
