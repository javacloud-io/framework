package io.javacloud.framework.i18n;

import io.javacloud.framework.cdi.ServiceTest;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author ho
 *
 */
public class MessageFactoryTest extends ServiceTest {
	@Inject
	MessageFactory messageFactory;
	
	@Test
	public void testMessages() {
		String message = messageFactory.getString("i18n.test.one");
		Assert.assertEquals("ONE", message);
	}
	
	@Test
	public void testBundles() {
	}
}
