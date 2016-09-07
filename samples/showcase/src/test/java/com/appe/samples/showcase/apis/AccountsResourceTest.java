/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.samples.showcase.apis;

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
