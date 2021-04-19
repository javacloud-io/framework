package javacloud.framework.i18n;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.cdi.junit.IntegrationTest;
/**
 * 
 * @author ho
 *
 */
public class MessageManagerIT extends IntegrationTest {
	@Inject
	MessageManager messageFactory;
	@Inject
	LocaleContext localeContext;
	
	@Test
	public void testMessages() {
		String message = messageFactory.getString("i18n.test.one");
		Assert.assertEquals("ONE", message);
	}
	
	@Test
	public void testBundles() {
		localeContext.set("en-US");
		Assert.assertEquals("en_US", localeContext.get().toString());
	}
}
