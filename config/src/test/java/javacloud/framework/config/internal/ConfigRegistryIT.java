package javacloud.framework.config.internal;

import java.util.Arrays;
import java.util.Properties;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.cdi.junit.IntegrationTest;
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
	
	/**
	 * 
	 */
	@Test
	public void testCliArgs() {
		Properties props = StandardConfigSource.parseProperties(new String[]{"-X", "-Y", "--xyz", "abc", "1", "2"});
		Assert.assertEquals("", props.get("X"));
		Assert.assertEquals("abc", props.get("xyz"));
		Assert.assertEquals(Arrays.asList("1", "2"), props.get(""));
	}
}
