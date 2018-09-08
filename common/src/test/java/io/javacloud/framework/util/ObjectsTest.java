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

import io.javacloud.framework.data.Converters;
import io.javacloud.framework.data.Dictionaries;
import io.javacloud.framework.data.Dictionary;
import io.javacloud.framework.util.Objects;

import org.junit.Assert;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class ObjectsTest extends TestCase {
	
	public void testCompare() {
		Assert.assertTrue(Objects.compare(1, 2) < 0);
		Assert.assertTrue(Objects.compare("aa", "ab") < 0);
	}
	
	public void testDict() {
		Dictionary dict = Dictionaries.asDict("a", "aa", "b", "bb");
		Assert.assertEquals(2, dict.size());
		Assert.assertEquals(dict.get("a"), "aa");
		Assert.assertEquals(dict.get("b"), "bb");
		Assert.assertNull(dict.get("c"));
	}
	
	public void testSignum() {
		Assert.assertEquals(1, Objects.signum(100));
		Assert.assertEquals(-1, Objects.signum(-10));
		Assert.assertEquals(0, Objects.signum(0));
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
