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
package io.javacloud.framework.util;

import junit.framework.TestCase;
/**
 * 
 * @author tobi
 *
 */
public class DictionariesTest extends TestCase {
	public void testPList() throws Exception {
		Dictionary props = new Dictionary();
		props.put("name1", "value1");
		props.put("name2", "value2");
		props.put("data", new byte[] {1, 2, 3, 4, 5, 6,7});
		props.put("null", null);	//ALOW NULL
		
		assertTrue(props.containsKey("null"));
		Dictionaries.writePlist(props, System.out);
	}
}
