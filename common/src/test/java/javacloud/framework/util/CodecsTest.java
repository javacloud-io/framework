package javacloud.framework.util;
import java.util.UUID;

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
		System.out.println("Base64:" + base64);
		assertEquals(text, new String(Codecs.Base64Decoder.apply(base64, false)));
	}
	
	public void testBase64Safe() {
		String text = UUID.randomUUID().toString();
		String base64 = Codecs.Base64Encoder.apply(text.getBytes(), true);
		System.out.println("Base64 safe:" + base64);
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
		System.out.println(Codecs.Base64Encoder.apply(sha, true));
	}
}
