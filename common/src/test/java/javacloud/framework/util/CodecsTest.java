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
import java.util.UUID;

import javacloud.framework.util.Codecs;
import javacloud.framework.util.Digests;
import junit.framework.TestCase;

/**
 * 
 * @author tobi
 *
 */
public class CodecsTest extends TestCase {
	public void testBase64() {
		String text = UUID.randomUUID().toString();
		String base64 = Codecs.Base64Encoder.apply(text.getBytes(), false);
		assertEquals(text, new String(Codecs.Base64Decoder.apply(base64, false)));
	}
	
	public void testBase64Safe() {
		String text = UUID.randomUUID().toString();
		String base64 = Codecs.Base64Encoder.apply(text.getBytes(), true);
		assertEquals(text, new String(Codecs.Base64Decoder.apply(base64, true)));
	}
	
	public void testBaseHex() {
		String text = UUID.randomUUID().toString();
		String hex = new Codecs.HexEncoder().apply(text.getBytes());
		assertEquals(text, new String(new Codecs.HexDecoder().apply(hex)));
	}
	
	public void testUUID() {
		long start = System.currentTimeMillis();
		for(int i = 0; i < 10000; i ++) {
			Codecs.randomID();
		}
		System.out.println("BID cost:" + (System.currentTimeMillis() - start));
		
		//UUID
		start = System.currentTimeMillis();
		for(int i = 0; i < 10000; i ++) {
			UUID.randomUUID().toString();
		}
		System.out.println("UUID cost:" + (System.currentTimeMillis() - start));
	}
	
	public void testSHA() {
		byte[] sha = Digests.digest(Digests.SHA1, new byte[0]);
		System.out.println(Codecs.Base64Encoder.apply(sha, false));
		
		sha = Digests.digest(Digests.SHA2, new byte[0]);
		System.out.println(Codecs.Base64Encoder.apply(sha, false));
	}
}
