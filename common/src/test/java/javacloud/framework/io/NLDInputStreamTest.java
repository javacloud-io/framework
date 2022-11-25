package javacloud.framework.io;

import java.nio.ByteBuffer;

import org.junit.Assert;

import junit.framework.TestCase;

public class NLDInputStreamTest extends TestCase {
	public void testLines() throws Exception {
		NLDInputStream ls = new NLDInputStream(new BytesInputStream("11\n22\n33\n44"));
		Assert.assertEquals("11", asString(ls.nextLine()));
		Assert.assertEquals(3, ls.offset());
		Assert.assertEquals("22", asString(ls.nextLine()));
		Assert.assertEquals(6, ls.offset());
		Assert.assertEquals("33", asString(ls.nextLine()));
		Assert.assertEquals(9, ls.offset());
		Assert.assertNull(ls.nextLine());
		Assert.assertEquals(11, ls.total());
	}
	
	public void testLinesEOL() throws Exception {
		NLDInputStream ls = new NLDInputStream(new BytesInputStream("11\n22\n33\n44"));
		Assert.assertEquals("11", asString(ls.nextLine()));
		Assert.assertEquals(3, ls.offset());
		Assert.assertEquals("22", asString(ls.nextLine()));
		Assert.assertEquals(6, ls.offset());
		Assert.assertEquals("33", asString(ls.nextLine()));
		Assert.assertEquals(9, ls.offset());
		Assert.assertEquals("44", asString(ls.nextLine(false)));
		Assert.assertEquals(11, ls.total());
	}
	
	static String asString(ByteBuffer buf) {
		return new String(buf.array(), buf.position(), buf.remaining());
	}
}
