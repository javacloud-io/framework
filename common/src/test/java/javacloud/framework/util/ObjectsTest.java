package javacloud.framework.util;

import java.util.List;

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
		Assert.assertTrue(Objects.compare(1f, 1.0f) == 0);
		Assert.assertTrue(Objects.compare("aa", "ab") < 0);
	}
	
	public void testSignum() {
		Assert.assertEquals(1, Objects.signum(100));
		Assert.assertEquals(-1, Objects.signum(-10));
		Assert.assertEquals(0, Objects.signum(0));
	}
	
	public void testObjects() {
		Object v = new String[] {"a", "b", "c"};
		List<Object> list = Objects.asList((Object[])v);
		Assert.assertEquals(3, list.size());
	}
	
	public void testSimilarity() {
		assertEquals(1.0f, Objects.similarity("a", "a"));
		assertEquals(1.0f, Objects.similarity("abc", "abc"));
		assertEquals(0.0f, Objects.similarity("abc", "bac"));
		assertTrue(Objects.similarity("abc", "a") > 0.3333);
		assertEquals(0.2f, Objects.similarity("night", "nacht"));
	}
	
	public void testEquals() {
		assertTrue(Objects.equals(null, null));
		assertFalse(Objects.equals(null, "xxx"));
		assertFalse(Objects.equals("", null));
		assertFalse(Objects.equals(123, "123"));
	}
}
