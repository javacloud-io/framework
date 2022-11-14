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
		String greetings = client.target("http://localhost:8081/v1/greetings")
				.request()
				.get(String.class);
		Assert.assertEquals("Hello world!", greetings);
	}
	
	@Test
	public void testHello() throws Exception {
		testGreetings();
	}
}
