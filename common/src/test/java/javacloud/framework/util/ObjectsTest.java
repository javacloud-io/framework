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

import java.util.List;

import org.junit.Assert;

import javacloud.framework.util.Converters;
import javacloud.framework.util.Objects;
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
	
	public void testObjects() {
		Object v = new String[] {"a", "b", "c"};
		List<Object> list = Objects.asList((Object[])v);
		Assert.assertEquals(3, list.size());
	}
}
