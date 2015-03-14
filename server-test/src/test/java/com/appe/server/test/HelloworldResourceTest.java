package com.appe.server.test;

import javax.ws.rs.core.Application;

import org.junit.Assert;

import org.junit.Test;

/**
 * 
 * @author ho
 *
 */
public class HelloworldResourceTest extends DefaultServerTest {
	@Override
	protected Application configure() {
		return new TestApplication();
	}
	
	@Test
	public void testGreetings() {
		String greetings = target("/v1/greetings").request().get(String.class);
		Assert.assertEquals("Hello world!", greetings);
	}
}
