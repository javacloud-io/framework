package com.appe.framework.server;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: all the basic test of exception
 * 
 * @author ho
 *
 */
public class ExceptionMapperTest {
	private GenericExceptionMapper<Throwable> mapper = new GenericExceptionMapper<Throwable>();
	
	@Test
	public void testEx() {
		Response rsp = mapper.toResponse(new Exception("it's a test exception"));
		Assert.assertEquals(500, rsp.getStatus());
	}
}
