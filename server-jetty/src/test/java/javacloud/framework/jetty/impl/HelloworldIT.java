package javacloud.framework.jetty.impl;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import javacloud.framework.cdi.internal.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;

/**
 * Using per class server test
 * 
 * @author ho
 *
 */
public class HelloworldIT extends IntegrationTest {
	
	@Test
	public void testGreetings() throws Exception {
		Client client = ClientBuilder.newBuilder().build();
		String greetings = client.target("http://localhost:8188/v1/greetings")
				.request()
				.get(String.class);
		Assert.assertEquals("Hello world!", greetings);
	}
	
	@Test
	public void testHello() throws Exception {
		testGreetings();
	}
}
