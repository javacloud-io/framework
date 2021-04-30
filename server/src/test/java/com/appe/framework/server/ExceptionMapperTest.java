package com.appe.framework.server;

import jakarta.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.server.ServerExceptionMapper;

/**
 * TODO: all the basic test of exception
 * 
 * @author ho
 *
 */
public class ExceptionMapperTest {
	private ServerExceptionMapper<Throwable> mapper = new ServerExceptionMapper<Throwable>(){};
	
	@Test
	public void testEx() {
		Response rsp = mapper.toResponse(new Exception("it's a test exception"));
		Assert.assertEquals(500, rsp.getStatus());
	}
}
