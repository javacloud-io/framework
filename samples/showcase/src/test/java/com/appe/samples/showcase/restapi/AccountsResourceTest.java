package com.appe.samples.showcase.restapi;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.appe.samples.showcase.startup.MainApplication;
import com.appe.server.test.IntegrationServerTest;

/**
 * 
 * @author ho
 *
 */
public class AccountsResourceTest extends IntegrationServerTest {
	/**
	 * 
	 */
	@Override
	protected Application configure() {
		return new MainApplication();
	}

	@Test
	public void testAccount() {
		Response rsp = target("/v1/accounts").request().get();
		Assert.assertEquals(403, rsp.getStatus());
	}
}
