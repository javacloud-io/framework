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
package javacloud.framework.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;

import junit.framework.TestCase;
/**
 * 
 * @author tobi
 *
 */
public class ConverterTest extends TestCase {
	public void testString() {
		String date = Converters.STRING.apply(new Date());
		Converters.DATE.apply(date);
	}
	
	public void testStrings() {
		String[] list = Converters.toObject("1,2,3,4", String[].class);
		assertEquals(4, list.length);
		assertEquals("2", list[1]);
	}
	
	public void testInts() {
		int[] list = Converters.toObject("1,2,3,4", int[].class);
		assertEquals(4, list.length);
		assertEquals(2, list[1]);
	}
	
	public void testList() {
		List<String> list = Converters.toObject("1,2,3,4", new ArrayList<String>().getClass());
		assertEquals(4, list.size());
		assertEquals("2", list.get(1));
	}
	
	public void testToString() {
		String s = Converters.toString(",", 1,2,3,4);
		Assert.assertEquals("1,2,3,4", s);
	}
	
	public void testToArray() {
		String[] a = Converters.toArray("1,2,3,4", ",", true);
		Assert.assertArrayEquals(new String[]{"1","2","3","4"}, a);
	}
}
