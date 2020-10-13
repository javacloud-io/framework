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
package javacloud.framework.server.test;

import javax.ws.rs.core.Application;

import org.junit.Assert;
import org.junit.Test;

/**
 * Using per class server test
 * 
 * @author ho
 *
 */
public class HelloworldApplicationTest extends ServiceApplicationTest {
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
