package javacloud.framework.server.internal;

import jakarta.ws.rs.core.Application;

import org.junit.Assert;
import org.junit.Test;

/**
 * Using per class server test
 * 
 * @author ho
 *
 */
public class HelloworldIT extends ServerIntegrationTest {
	@Override
	protected Application configure() {
		return new TestApplication();
	}
	
	@Test
	public void testGreetings() {
		String greetings = target("/v1/greetings").request().get(String.class);
		Assert.assertEquals("Hello world!", greetings);
	}
	
	@Test
	public void testHello() {
		testGreetings();
	}
}
